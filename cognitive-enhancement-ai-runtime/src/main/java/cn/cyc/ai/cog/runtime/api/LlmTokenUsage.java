package cn.cyc.ai.cog.runtime.api;

/**
 * LLM 调用 token 用量摘要。
 *
 * @param inputTokenCount  输入 token 数
 * @param outputTokenCount 输出 token 数
 * @param totalTokenCount  总 token 数
 * @author cyc
 */
public record LlmTokenUsage(int inputTokenCount, int outputTokenCount, int totalTokenCount) {

    /**
     * 空用量占位。
     */
    public static final LlmTokenUsage EMPTY = new LlmTokenUsage(0, 0, 0);
}
