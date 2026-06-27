package cn.cyc.ai.cog.platform.billing.support;

/**
 * 计费相关安全配置键常量。
 */
public final class BillingConfigKeys {

    private BillingConfigKeys() {
    }

    /** 待支付超时分钟数，默认 30。 */
    public static final String ORDER_PENDING_TIMEOUT_MINUTES = "order.pendingTimeoutMinutes";

    /** 订阅退款是否回收会员，默认 true。 */
    public static final String REFUND_REVOKE_MEMBERSHIP = "refund.revokeMembership";

    /** 额度包退款是否扣回未用加油额度，默认 true。 */
    public static final String REFUND_CLAWBACK_UNUSED = "refund.clawbackUnused";
}
