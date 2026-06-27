package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.domain.QuotaPackage;
import cn.cyc.ai.cog.platform.billing.domain.Subscription;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.platform.billing.entity.SubscriptionPhase;
import cn.cyc.ai.cog.platform.billing.repository.FinancialRecordRepository;
import cn.cyc.ai.cog.platform.billing.repository.OrderRepository;
import cn.cyc.ai.cog.platform.billing.repository.QuotaPackageRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionPackageRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionRepository;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.service.MembershipLevelService;
import cn.cyc.ai.cog.platform.quota.service.QuotaService;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 订单支付后权益发放：订阅 / 加油包，幂等按 orderNo。
 */
@Service
public class FulfillmentService {

    /** 订单仓储 */
    private final OrderRepository orderRepository;

    /** 订阅记录仓储 */
    private final SubscriptionRepository subscriptionRepository;

    /** 订阅套餐仓储 */
    private final SubscriptionPackageRepository subscriptionPackageRepository;

    /** 额度包仓储 */
    private final QuotaPackageRepository quotaPackageRepository;

    /** 账户会员关系仓储 */
    private final AccountMembershipRepository accountMembershipRepository;

    /** 会员等级仓储 */
    private final MembershipLevelRepository membershipLevelRepository;

    /** 会员等级变更日志服务 */
    private final MembershipLevelService membershipLevelService;

    /** 额度服务 */
    private final QuotaService quotaService;

    /** 资金流水仓储 */
    private final FinancialRecordRepository financialRecordRepository;

    /**
     * @param orderRepository                 订单仓储
     * @param subscriptionRepository          订阅记录仓储
     * @param subscriptionPackageRepository   订阅套餐仓储
     * @param quotaPackageRepository          额度包仓储
     * @param accountMembershipRepository     账户会员关系仓储
     * @param membershipLevelRepository       会员等级仓储
     * @param membershipLevelService          会员等级变更日志服务
     * @param quotaService                    额度服务
     * @param financialRecordRepository       资金流水仓储
     */
    public FulfillmentService(OrderRepository orderRepository,
                              SubscriptionRepository subscriptionRepository,
                              SubscriptionPackageRepository subscriptionPackageRepository,
                              QuotaPackageRepository quotaPackageRepository,
                              AccountMembershipRepository accountMembershipRepository,
                              MembershipLevelRepository membershipLevelRepository,
                              MembershipLevelService membershipLevelService,
                              QuotaService quotaService,
                              FinancialRecordRepository financialRecordRepository) {
        this.orderRepository = orderRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPackageRepository = subscriptionPackageRepository;
        this.quotaPackageRepository = quotaPackageRepository;
        this.accountMembershipRepository = accountMembershipRepository;
        this.membershipLevelRepository = membershipLevelRepository;
        this.membershipLevelService = membershipLevelService;
        this.quotaService = quotaService;
        this.financialRecordRepository = financialRecordRepository;
    }

    /**
     * 发放订单权益（订阅或加油包），幂等按订单号。
     *
     * @param orderId 订单 ID
     * @return 发放完成后的订单
     */
    /**
     * 发放订单权益（订阅或加油包），幂等按订单号。
     *
     * @param orderId 订单 ID
     * @return 发放完成后的订单
     */
    @Transactional
    public Order fulfill(Long orderId) {
        Order order = orderRepository.requireById(orderId);
        if (OrderStatus.FULFILLED.matches(order.status())) {
            return order;
        }
        if (!OrderStatus.PAID.matches(order.status())) {
            throw Errors.of(PlatformErrorCode.ORDER_PAID_ONLY_FULFILL);
        }
        String idem = "fulfill:" + order.orderNo();
        if ("SUBSCRIPTION".equals(order.orderType())) {
            fulfillSubscription(order, idem);
        } else if ("QUOTA".equals(order.orderType())) {
            fulfillQuotaPackage(order, idem);
        } else {
            throw Errors.of(PlatformErrorCode.ORDER_TYPE_UNKNOWN, "未知订单类型：" + order.orderType());
        }
        Order fulfilled = new Order(
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
                OrderStatus.FULFILLED.code(),
                order.payChannel(),
                order.payTime(),
                LocalDateTime.now(),
                order.idempotencyKey(),
                order.refundAmountFen(),
                order.refundTime(),
                order.remark()
        );
        orderRepository.update(fulfilled);
        financialRecordRepository.insertPayment(order.tenantId(), order.accountId(), order.id(), order.amountFen(), "订单发放完成");
        return fulfilled;
    }

    private void fulfillSubscription(Order order, String idem) {
        SubscriptionPackage pkg = subscriptionPackageRepository.requireById(order.packageId());
        MembershipLevel level = pkg.levelId() == null ? null : membershipLevelRepository.findById(pkg.levelId());
        LocalDateTime start = LocalDateTime.now();
        int trialDays = pkg.trialDays() == null ? 0 : pkg.trialDays();
        String phase;
        LocalDateTime end;
        if (trialDays > 0) {
            phase = SubscriptionPhase.TRIAL;
            end = start.plusDays(trialDays);
        } else {
            phase = SubscriptionPhase.FORMAL;
            end = start.plusMonths(Math.max(1, pkg.periodCount() == null ? 1 : pkg.periodCount()));
        }
        String levelCode = level == null ? pkg.packageCode() : level.levelCode();

        Subscription sub = subscriptionRepository.insertActive(
                order.tenantId(), order.accountId(), order.id(), pkg.id(), levelCode, phase, start, end);

        AccountMembership membership = accountMembershipRepository.findByAccountId(order.accountId());
        String fromLevel = membership == null ? null : membership.levelCode();
        accountMembershipRepository.upsertGrant(order.accountId(), pkg.levelId(), sub.levelCode(), end, "SUBSCRIPTION");

        membershipLevelService.writeChangeLog(order.accountId(), fromLevel, sub.levelCode(), "PURCHASE", order.orderNo());
        long cycleQuota = pkg.cycleTokenQuota() == null ? 0L : pkg.cycleTokenQuota();
        if (cycleQuota > 0) {
            quotaService.grantCycleQuota(order.accountId(), cycleQuota, idem + ":cycle", "订阅发放");
        }
    }

    private void fulfillQuotaPackage(Order order, String idem) {
        QuotaPackage pkg = quotaPackageRepository.requireById(order.packageId());
        long tokens = pkg.tokenAmount() == null ? 0L : pkg.tokenAmount();
        quotaService.grantTopupQuota(order.accountId(), tokens, idem + ":topup", "加油包发放");
    }
}
