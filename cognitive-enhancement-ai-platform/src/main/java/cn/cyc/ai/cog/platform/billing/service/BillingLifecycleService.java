package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.domain.Subscription;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.platform.billing.repository.OrderRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionPackageRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionRepository;
import cn.cyc.ai.cog.platform.billing.support.BillingConfigKeys;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.service.MembershipLevelService;
import cn.cyc.ai.cog.platform.membership.service.MembershipOnboardingService;
import cn.cyc.ai.cog.platform.membership.support.MembershipBenefitSupport;
import cn.cyc.ai.cog.platform.quota.domain.QuotaAccount;
import cn.cyc.ai.cog.platform.quota.repository.QuotaAccountRepository;
import cn.cyc.ai.cog.platform.quota.service.QuotaService;
import cn.cyc.ai.cog.platform.system.service.SecurityConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 计费生命周期服务：订阅过期降级与周期额度重置。
 */
@Service
public class BillingLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(BillingLifecycleService.class);
    private static final String FREE_LEVEL_CODE = "FREE";

    /** 订阅记录仓储 */
    private final SubscriptionRepository subscriptionRepository;

    /** 订阅套餐仓储 */
    private final SubscriptionPackageRepository subscriptionPackageRepository;

    /** 账户会员关系仓储 */
    private final AccountMembershipRepository accountMembershipRepository;

    /** 会员等级仓储 */
    private final MembershipLevelRepository membershipLevelRepository;

    /** 会员等级变更日志服务 */
    private final MembershipLevelService membershipLevelService;

    /** 额度账户仓储 */
    private final QuotaAccountRepository quotaAccountRepository;

    /** 订单仓储 */
    private final OrderRepository orderRepository;

    /** 安全配置服务 */
    private final SecurityConfigService securityConfigService;

    /** 额度服务 */
    private final QuotaService quotaService;

    /** 会员权益解析 */
    private final MembershipBenefitSupport membershipBenefitSupport;

    /**
     * @param subscriptionRepository          订阅记录仓储
     * @param subscriptionPackageRepository   订阅套餐仓储
     * @param accountMembershipRepository     账户会员关系仓储
     * @param membershipLevelRepository       会员等级仓储
     * @param membershipLevelService          会员等级变更日志服务
     * @param quotaAccountRepository          额度账户仓储
     * @param orderRepository                 订单仓储
     * @param securityConfigService           安全配置服务
     * @param quotaService                    额度服务
     * @param membershipBenefitSupport        会员权益解析
     */
    public BillingLifecycleService(SubscriptionRepository subscriptionRepository,
                                   SubscriptionPackageRepository subscriptionPackageRepository,
                                   AccountMembershipRepository accountMembershipRepository,
                                   MembershipLevelRepository membershipLevelRepository,
                                   MembershipLevelService membershipLevelService,
                                   QuotaAccountRepository quotaAccountRepository,
                                   OrderRepository orderRepository,
                                   SecurityConfigService securityConfigService,
                                   QuotaService quotaService,
                                   MembershipBenefitSupport membershipBenefitSupport) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPackageRepository = subscriptionPackageRepository;
        this.accountMembershipRepository = accountMembershipRepository;
        this.membershipLevelRepository = membershipLevelRepository;
        this.membershipLevelService = membershipLevelService;
        this.quotaAccountRepository = quotaAccountRepository;
        this.orderRepository = orderRepository;
        this.securityConfigService = securityConfigService;
        this.quotaService = quotaService;
        this.membershipBenefitSupport = membershipBenefitSupport;
    }

    /**
     * 将已到期订阅标记过期并降级会员等级。
     *
     * @return 处理的订阅数量
     */
    @Transactional
    public int expireSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;
        for (Subscription sub : subscriptionRepository.listTrialExpiredActive(now)) {
            advanceTrialToFormal(sub, now);
            count++;
        }
        for (Subscription sub : subscriptionRepository.listFormalExpiredActive(now)) {
            subscriptionRepository.markExpired(sub.id());
            downgradeMembership(sub.accountId());
            count++;
        }
        if (count > 0) {
            log.info("订阅生命周期处理完成，数量={}", count);
        }
        return count;
    }

    private void advanceTrialToFormal(Subscription sub, LocalDateTime now) {
        SubscriptionPackage pkg = sub.packageId() == null
                ? null : subscriptionPackageRepository.requireById(sub.packageId());
        int months = pkg == null || pkg.periodCount() == null ? 1 : Math.max(1, pkg.periodCount());
        LocalDateTime formalEnd = now.plusMonths(months);
        subscriptionRepository.advanceToFormal(sub.id(), formalEnd);

        AccountMembership membership = accountMembershipRepository.findByAccountId(sub.accountId());
        if (membership != null) {
            accountMembershipRepository.upsertGrant(
                    sub.accountId(), membership.levelId(), membership.levelCode(), formalEnd, "TRIAL_CONVERT");
        }

        long cycleQuota = resolveCycleQuota(sub.accountId(), now);
        if (cycleQuota > 0) {
            quotaService.grantCycleQuota(sub.accountId(), cycleQuota,
                    "trial-convert:" + sub.id(), "试用转正发放");
        }
        log.info("订阅试用转正，subscriptionId={}, accountId={}", sub.id(), sub.accountId());
    }

    /**
     * 重置到期账户的周期额度。
     *
     * @return 重置的账户数量
     */
    @Transactional
    public int resetCycleQuotas() {
        LocalDateTime now = LocalDateTime.now();
        List<QuotaAccount> due = quotaAccountRepository.listDueForCycleReset(now);
        int count = 0;
        for (QuotaAccount quota : due) {
            long cycleQuota = resolveCycleQuota(quota.accountId(), now);
            LocalDateTime nextResetAt = MembershipOnboardingService.nextMonthStart(now.toLocalDate());
            QuotaAccount updated = new QuotaAccount(
                    quota.id(),
                    quota.tenantId(),
                    quota.accountId(),
                    cycleQuota,
                    cycleQuota,
                    nextResetAt,
                    quota.giftRemaining(),
                    quota.giftTotal(),
                    quota.topupRemaining(),
                    quota.topupTotal()
            );
            quotaAccountRepository.update(updated);
            count++;
        }
        if (count > 0) {
            log.info("周期额度重置完成，数量={}", count);
        }
        return count;
    }

    /**
     * 关闭超时未支付的待支付订单。
     *
     * @return 关闭的订单数量
     */
    @Transactional
    public int closeExpiredPendingOrders() {
        int timeoutMinutes = securityConfigService.getInt(BillingConfigKeys.ORDER_PENDING_TIMEOUT_MINUTES, 30);
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(timeoutMinutes);
        List<Order> expired = orderRepository.listPendingCreatedBefore(deadline);
        int count = 0;
        for (Order order : expired) {
            Order closed = new Order(
                    order.id(),
                    order.tenantId(),
                    order.orderNo(),
                    order.accountId(),
                    order.buyerUserId(),
                    order.orderType(),
                    order.packageId(),
                    order.packageSnapshotJson(),
                    order.amountFen(),
                    order.currency(),
                    OrderStatus.CLOSED.code(),
                    order.payChannel(),
                    order.payTime(),
                    order.fulfillTime(),
                    order.idempotencyKey(),
                    order.refundAmountFen(),
                    order.refundTime(),
                    order.remark()
            );
            orderRepository.update(closed);
            count++;
        }
        if (count > 0) {
            log.info("待支付超时关闭完成，数量={}，超时分钟={}", count, timeoutMinutes);
        }
        return count;
    }

    private void downgradeMembership(Long accountId) {
        AccountMembership membership = accountMembershipRepository.findByAccountId(accountId);
        if (membership == null) {
            return;
        }
        MembershipLevel free = membershipLevelRepository.findByCodeIfPresent("FREE");
        if (free == null) {
            return;
        }
        String from = membership.levelCode();
        accountMembershipRepository.upsertGrant(accountId, free.id(), free.levelCode(), null, "EXPIRE");
        membershipLevelService.writeChangeLog(accountId, from, free.levelCode(), "EXPIRE", "subscription-expired");
    }

    private long resolveCycleQuota(Long accountId, LocalDateTime now) {
        Subscription active = subscriptionRepository.findActiveValidByAccountId(accountId, now);
        if (active != null && active.packageId() != null) {
            SubscriptionPackage pkg = subscriptionPackageRepository.requireById(active.packageId());
            if (pkg.cycleTokenQuota() != null && pkg.cycleTokenQuota() > 0) {
                return pkg.cycleTokenQuota();
            }
        }
        AccountMembership membership = accountMembershipRepository.findByAccountId(accountId);
        if (membership != null && FREE_LEVEL_CODE.equalsIgnoreCase(membership.levelCode())) {
            MembershipLevel level = membershipLevelRepository.findById(membership.levelId());
            if (level != null) {
                return membershipBenefitSupport.resolveMonthlyTokenQuota(level.id(), level.benefitsJson());
            }
        }
        return MembershipBenefitSupport.DEFAULT_MONTHLY_TOKEN_QUOTA;
    }
}
