package cn.cyc.ai.cog.runtime.api;

/**
 * LLM 调用 token 用量摘要。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record LlmTokenUsage(int inputTokenCount, int outputTokenCount, int totalTokenCount) {

    /**
     * 空用量占位。
     */
    public static final LlmTokenUsage EMPTY = new LlmTokenUsage(0, 0, 0);
}
