package cn.cyc.ai.cog.runtime.api;

import java.util.List;

/**
 * 多轮对话 LLM 响应。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 判断是否包含工具Calls。
     * @return 是否包含
     */
    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }
}
