package cn.cyc.ai.cog.platform.billing.entity;

/**
 * 订阅周期阶段常量。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class SubscriptionPhase {

    /**
     * 创建SubscriptionPhase。
     */
    private SubscriptionPhase() {
    }

    /** 试用期。 */
    public static final String TRIAL = "TRIAL";

    /** 正式周期。 */
    public static final String FORMAL = "FORMAL";
}
