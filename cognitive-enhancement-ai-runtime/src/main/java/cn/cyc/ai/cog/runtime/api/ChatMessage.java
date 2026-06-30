package cn.cyc.ai.cog.runtime.api;

import java.util.List;

/**
 * OpenAI-compatible 对话消息。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ChatMessage(
        String role,
        String content,
        List<LlmToolCall> toolCalls,
        String toolCallId,
        String name
) {

    /**
     * 执行system。
     *
     * @param content 内容
     * @return 执行结果
     */
    public static ChatMessage system(String content) {
        return new ChatMessage("system", content, List.of(), null, null);
    }

    /**
     * 执行用户。
     *
     * @param content 内容
     * @return 执行结果
     */
    public static ChatMessage user(String content) {
        return new ChatMessage("user", content, List.of(), null, null);
    }

    /**
     * 执行assistant。
     *
     * @param content 内容
     * @param toolCalls 工具Calls
     * @return 执行结果
     */
    public static ChatMessage assistant(String content, List<LlmToolCall> toolCalls) {
        return new ChatMessage("assistant", content, toolCalls == null ? List.of() : toolCalls, null, null);
    }

    /**
     * 转换为ol。
     *
     * @param toolCallId 工具CallID
     * @param name 名称
     * @param content 内容
     * @return 转换结果
     */
    public static ChatMessage tool(String toolCallId, String name, String content) {
        return new ChatMessage("tool", content, List.of(), toolCallId, name);
    }
}
