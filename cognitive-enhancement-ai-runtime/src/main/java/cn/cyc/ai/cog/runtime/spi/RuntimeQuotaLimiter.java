package cn.cyc.ai.cog.runtime.spi;

/**
 * 运行时限流与配额拦截器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface RuntimeQuotaLimiter {

    /**
     * 检查并消费一次能力调用配额。
     *
     * @param capabilityCode 能力编码
     */
    void checkAndConsume(String capabilityCode);
}
