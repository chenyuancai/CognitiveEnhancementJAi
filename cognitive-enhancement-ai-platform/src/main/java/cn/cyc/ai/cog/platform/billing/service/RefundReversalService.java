package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.domain.QuotaPackage;
import cn.cyc.ai.cog.platform.billing.domain.Subscription;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.platform.billing.repository.FinancialRecordRepository;
import cn.cyc.ai.cog.platform.billing.repository.OrderRepository;
import cn.cyc.ai.cog.platform.billing.repository.QuotaPackageRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionPackageRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionRepository;
import cn.cyc.ai.cog.platform.billing.support.BillingConfigKeys;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.service.MembershipLevelService;
import cn.cyc.ai.cog.platform.quota.service.QuotaService;
import cn.cyc.ai.cog.platform.system.service.SecurityConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 退款逆向：撤销已发放权益（订阅 / 加油包）。
 */
@Service
public class RefundReversalService {

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

    /** 安全配置服务 */
    private final SecurityConfigService securityConfigService;

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
     * @param securityConfigService           安全配置服务
     */
    public RefundReversalService(OrderRepository orderRepository,
                                 SubscriptionRepository subscriptionRepository,
                                 SubscriptionPackageRepository subscriptionPackageRepository,
                                 QuotaPackageRepository quotaPackageRepository,
                                 AccountMembershipRepository accountMembershipRepository,
                                 MembershipLevelRepository membershipLevelRepository,
                                 MembershipLevelService membershipLevelService,
                                 QuotaService quotaService,
                                 FinancialRecordRepository financialRecordRepository,
                                 SecurityConfigService securityConfigService) {
        this.orderRepository = orderRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPackageRepository = subscriptionPackageRepository;
        this.quotaPackageRepository = quotaPackageRepository;
        this.accountMembershipRepository = accountMembershipRepository;
        this.membershipLevelRepository = membershipLevelRepository;
        this.membershipLevelService = membershipLevelService;
        this.quotaService = quotaService;
        this.financialRecordRepository = financialRecordRepository;
        this.securityConfigService = securityConfigService;
    }

    /**
     * 撤销已发放订单的权益并标记退款。
     *
     * @param orderId          订单 ID
     * @param refundAmountFen  退款金额（分）
     * @return 退款后的订单
     */
    @Transactional
    public Order reverse(Long orderId, long refundAmountFen) {
        Order order = orderRepository.requireById(orderId);
        if (!OrderStatus.FULFILLED.matches(order.status())) {
            return order;
        }
        String idem = "refund:" + order.orderNo();
        if ("SUBSCRIPTION".equals(order.orderType())) {
            reverseSubscription(order, idem);
        } else if ("QUOTA".equals(order.orderType())) {
            reverseQuotaPackage(order, idem);
        }
        Order refunded = new Order(
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
                OrderStatus.REFUNDED.code(),
                order.payChannel(),
                order.payTime(),
                order.fulfillTime(),
                order.idempotencyKey(),
                refundAmountFen,
                LocalDateTime.now(),
                order.remark()
        );
        orderRepository.update(refunded);
        financialRecordRepository.insertRefund(order.tenantId(), order.accountId(), order.id(), refundAmountFen, "订单退款");
        return refunded;
    }

    private void reverseSubscription(Order order, String idem) {
        Subscription sub = subscriptionRepository.findByOrderId(order.id());
        if (sub != null) {
            subscriptionRepository.markRefunded(sub.id());
        }

        SubscriptionPackage pkg = subscriptionPackageRepository.requireById(order.packageId());
        long cycleQuota = pkg.cycleTokenQuota() == null ? 0L : pkg.cycleTokenQuota();
        if (cycleQuota > 0) {
            quotaService.revokeCycleQuota(order.accountId(), cycleQuota, idem + ":cycle", "订阅退款回收");
        }

        if (!securityConfigService.getBoolean(BillingConfigKeys.REFUND_REVOKE_MEMBERSHIP, true)) {
            return;
        }

        AccountMembership membership = accountMembershipRepository.findByAccountId(order.accountId());
        if (membership == null) {
            return;
        }
        MembershipLevel free = membershipLevelRepository.findByCodeIfPresent("FREE");
        if (free == null) {
            return;
        }
        String fromLevel = membership.levelCode();
        accountMembershipRepository.upsertGrant(order.accountId(), free.id(), free.levelCode(), null, "REFUND");
        membershipLevelService.writeChangeLog(order.accountId(), fromLevel, free.levelCode(), "REFUND", order.orderNo());
    }

    private void reverseQuotaPackage(Order order, String idem) {
        if (!securityConfigService.getBoolean(BillingConfigKeys.REFUND_CLAWBACK_UNUSED, true)) {
            return;
        }
        QuotaPackage pkg = quotaPackageRepository.requireById(order.packageId());
        long tokens = pkg.tokenAmount() == null ? 0L : pkg.tokenAmount();
        if (tokens > 0) {
            quotaService.revokeTopupQuota(order.accountId(), tokens, idem + ":topup", "加油包退款回收");
        }
    }
}
