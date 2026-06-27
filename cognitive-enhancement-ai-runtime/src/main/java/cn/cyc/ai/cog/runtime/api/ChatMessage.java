package cn.cyc.ai.cog.runtime.api;

import java.util.List;

/**
 * OpenAI-compatible 对话消息。
 */
public record ChatMessage(
        String role,
        String content,
        List<LlmToolCall> toolCalls,
        String toolCallId,
        String name
) {

    public static ChatMessage system(String content) {
        return new ChatMessage("system", content, List.of(), null, null);
    }

    public static ChatMessage user(String content) {
        return new ChatMessage("user", content, List.of(), null, null);
    }

    public static ChatMessage assistant(String content, List<LlmToolCall> toolCalls) {
        return new ChatMessage("assistant", content, toolCalls == null ? List.of() : toolCalls, null, null);
    }

    public static ChatMessage tool(String toolCallId, String name, String content) {
        return new ChatMessage("tool", content, List.of(), toolCallId, name);
    }
}
