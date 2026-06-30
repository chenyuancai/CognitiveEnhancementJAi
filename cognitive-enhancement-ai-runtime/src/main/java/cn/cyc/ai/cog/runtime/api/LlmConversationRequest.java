package cn.cyc.ai.cog.runtime.api;

import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;

import java.util.List;
import java.util.Map;

/**
 * 多轮对话 + 工具 schema 的 LLM 请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record LlmConversationRequest(
        List<ChatMessage> messages,
        List<ToolDefinition> tools,
        Map<String, Object> parameters,
        int timeoutMs
) {
}
