package cn.cyc.ai.cog.runtime.api;

import java.util.List;

/**
 * 多轮对话 LLM 响应。
 */
public record LlmConversationResult(
        String content,
        List<LlmToolCall> toolCalls,
        String finishReason,
        int inputTokenCount,
        int outputTokenCount,
        int totalTokenCount,
        long latencyMs,
        boolean mock
) {

    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }
}
