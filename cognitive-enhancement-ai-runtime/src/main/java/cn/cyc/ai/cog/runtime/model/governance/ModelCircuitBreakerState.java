package cn.cyc.ai.cog.runtime.model.governance;

/**
 * 模型熔断器状态。
 *
 * @author cyc
 */
public enum ModelCircuitBreakerState {

    /**
     * 关闭：正常调用主模型。
     */
    CLOSED,

    /**
     * 打开：拒绝主模型，走降级。
     */
    OPEN,

    /**
     * 半开：允许一次探测主模型。
     */
    HALF_OPEN
}
