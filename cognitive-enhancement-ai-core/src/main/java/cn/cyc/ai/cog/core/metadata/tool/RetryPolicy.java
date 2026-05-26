package cn.cyc.ai.cog.core.metadata.tool;

/**
 * 工具重试策略值对象。
 */
public record RetryPolicy(int maxAttempts) {

    public RetryPolicy {
        if (maxAttempts < 0) {
            throw new IllegalArgumentException("maxAttempts 不能小于 0");
        }
    }
}
