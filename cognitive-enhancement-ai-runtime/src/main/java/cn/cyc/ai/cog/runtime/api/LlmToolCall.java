package cn.cyc.ai.cog.runtime.api;

/**
 * LLM 返回的工具调用项。
 */
public record LlmToolCall(
        String id,
        String name,
        String arguments
) {
}
