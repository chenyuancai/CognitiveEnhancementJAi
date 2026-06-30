package cn.cyc.ai.cog.runtime.api;

/**
 * LLM 返回的工具调用项。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record LlmToolCall(
        String id,
        String name,
        String arguments
) {
}
