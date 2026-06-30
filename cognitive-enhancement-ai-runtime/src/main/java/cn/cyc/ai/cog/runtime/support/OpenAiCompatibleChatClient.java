package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.runtime.api.ChatMessage;
import cn.cyc.ai.cog.runtime.api.LlmConversationRequest;
import cn.cyc.ai.cog.runtime.api.LlmConversationResult;
import cn.cyc.ai.cog.runtime.api.LlmHttpRequest;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmTokenUsage;
import cn.cyc.ai.cog.runtime.api.LlmToolCall;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAI-compatible Chat Completions 协议客户端。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class OpenAiCompatibleChatClient {

    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;
    /** llmHttpExecutor。 */
    private final LlmHttpExecutor llmHttpExecutor;

    /**
     * 创建OpenAiCompatibleChat客户端。
     *
     * @param objectMapper JSON 序列化器
     * @param llmHttpExecutor llmHttpExecutor
     */
    public OpenAiCompatibleChatClient(ObjectMapper objectMapper, LlmHttpExecutor llmHttpExecutor) {
        this.objectMapper = objectMapper;
        this.llmHttpExecutor = llmHttpExecutor;
    }

    /**
     * 调用 Chat Completions 接口并解析标准响应。
     *
     * @param request 调用上下文
     * @param apiKey  API Key
     * @param path    兼容协议路径
     * @return 解析后的回答、用量与延迟
     */
    public ChatCompletionResult complete(LlmInvocationRequest request, String apiKey, String path) {
        String endpoint = resolveEndpoint(request.endpoint(), path);
        String requestBody = buildRequestBody(request);
        long startTime = System.currentTimeMillis();
        LlmHttpResponse response = llmHttpExecutor.execute(new LlmHttpRequest(
                endpoint,
                Map.of("Authorization", "Bearer " + apiKey),
                requestBody,
                Duration.ofMillis(request.timeoutMs())
        ));
        long latencyMs = System.currentTimeMillis() - startTime;
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new BusinessException("CONFLICT", "OpenAI-compatible 模型调用失败，HTTP 状态码: " + response.statusCode());
        }
        return parseResponse(response.body(), latencyMs);
    }

    /**
     * 多轮对话 + tools（ReAct）：仅返回 tool_calls，不在客户端内执行工具。
     */
    public LlmConversationResult completeConversation(String modelCode,
                                                      String endpoint,
                                                      String apiKey,
                                                      LlmConversationRequest request,
                                                      String path) {
        String resolvedEndpoint = resolveEndpoint(endpoint, path);
        String requestBody = buildConversationBody(modelCode, request);
        long startTime = System.currentTimeMillis();
        LlmHttpResponse response = llmHttpExecutor.execute(new LlmHttpRequest(
                resolvedEndpoint,
                Map.of("Authorization", "Bearer " + apiKey),
                requestBody,
                Duration.ofMillis(request.timeoutMs())
        ));
        long latencyMs = System.currentTimeMillis() - startTime;
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new BusinessException("CONFLICT", "OpenAI-compatible 模型调用失败，HTTP 状态码: " + response.statusCode());
        }
        return parseConversationResponse(response.body(), latencyMs);
    }

    /**
     * 执行resolveEndpoint。
     *
     * @param endpoint endpoint
     * @param path 路径
     * @return 执行结果
     */
    private String resolveEndpoint(String endpoint, String path) {
        if (!StringUtils.hasText(endpoint)) {
            throw new BusinessException("INVALID_ARGUMENT", "OpenAI-compatible endpoint 不能为空");
        }
        String normalizedPath = StringUtils.hasText(path) ? path : "/chat/completions";
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        if (endpoint.endsWith(normalizedPath)) {
            return endpoint;
        }
        if (endpoint.endsWith("/")) {
            return endpoint.substring(0, endpoint.length() - 1) + normalizedPath;
        }
        return endpoint + normalizedPath;
    }

    /**
     * 构建请求Body。
     *
     * @param request 请求
     * @return 构建结果
     */
    private String buildRequestBody(LlmInvocationRequest request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", request.modelCode());
        payload.put("messages", List.of(Map.of(
                "role", "user",
                "content", String.valueOf(request.promptInput())
        )));
        appendParameter(payload, request.parameters(), "temperature", "temperature");
        appendParameter(payload, request.parameters(), "topP", "top_p");
        appendParameter(payload, request.parameters(), "maxTokens", "max_tokens");
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new BusinessException("CONFLICT",
                    "OpenAI-compatible 请求体序列化失败: " + exception.getMessage(), exception);
        }
    }

    /**
     * 执行appendParameter。
     */
    private void appendParameter(Map<String, Object> payload,
                                 Map<String, Object> parameters,
                                 String sourceKey,
                                 String payloadKey) {
        if (parameters == null || !parameters.containsKey(sourceKey)) {
            return;
        }
        payload.put(payloadKey, parameters.get(sourceKey));
    }

    /**
     * 构建ConversationBody。
     *
     * @param modelCode 模型编码
     * @param request 请求
     * @return 构建结果
     */
    private String buildConversationBody(String modelCode, LlmConversationRequest request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", modelCode);
        payload.put("messages", toOpenAiMessages(request.messages()));
        if (request.tools() != null && !request.tools().isEmpty()) {
            payload.put("tools", ToolSchemaConverter.toOpenAiTools(request.tools()));
            payload.put("tool_choice", "auto");
        }
        appendParameter(payload, request.parameters(), "temperature", "temperature");
        appendParameter(payload, request.parameters(), "topP", "top_p");
        appendParameter(payload, request.parameters(), "maxTokens", "max_tokens");
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new BusinessException("CONFLICT",
                    "OpenAI-compatible 请求体序列化失败: " + exception.getMessage(), exception);
        }
    }

    private List<Map<String, Object>> toOpenAiMessages(List<ChatMessage> messages) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatMessage message : messages) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("role", message.role());
            if ("assistant".equals(message.role()) && message.toolCalls() != null && !message.toolCalls().isEmpty()) {
                if (StringUtils.hasText(message.content())) {
                    item.put("content", message.content());
                }
                item.put("tool_calls", message.toolCalls().stream().map(toolCall -> {
                    Map<String, Object> call = new LinkedHashMap<>();
                    call.put("id", toolCall.id());
                    call.put("type", "function");
                    Map<String, Object> function = new LinkedHashMap<>();
                    function.put("name", toolCall.name());
                    function.put("arguments", toolCall.arguments());
                    call.put("function", function);
                    return call;
                }).toList());
            } else if ("tool".equals(message.role())) {
                item.put("tool_call_id", message.toolCallId());
                item.put("name", message.name());
                item.put("content", message.content());
            } else {
                item.put("content", message.content());
            }
            result.add(item);
        }
        return result;
    }

    /**
     * 执行parseConversation响应。
     *
     * @param responseBody 响应Body
     * @param latencyMs latencyMs
     * @return 执行结果
     */
    private LlmConversationResult parseConversationResponse(String responseBody, long latencyMs) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode messageNode = rootNode.path("choices").path(0).path("message");
            if (messageNode.isMissingNode()) {
                throw new BusinessException("CONFLICT", "OpenAI-compatible 响应中缺少 choices[0].message");
            }
            String content = messageNode.path("content").isNull() || !messageNode.path("content").isTextual()
                    ? ""
                    : messageNode.path("content").asText();
            List<LlmToolCall> toolCalls = new ArrayList<>();
            JsonNode toolCallsNode = messageNode.path("tool_calls");
            if (toolCallsNode.isArray()) {
                for (JsonNode toolCallNode : toolCallsNode) {
                    toolCalls.add(parseToolCall(toolCallNode));
                }
            }
            if (!StringUtils.hasText(content) && toolCalls.isEmpty()) {
                throw new BusinessException("CONFLICT",
                        "OpenAI-compatible 响应中缺少 message.content 或 tool_calls");
            }
            LlmTokenUsage tokenUsage = OpenAiCompatibleUsageParser.parseUsage(rootNode);
            String finishReason = rootNode.path("choices").path(0).path("finish_reason").asText("");
            return new LlmConversationResult(
                    content,
                    toolCalls,
                    finishReason,
                    tokenUsage.inputTokenCount(),
                    tokenUsage.outputTokenCount(),
                    tokenUsage.totalTokenCount(),
                    latencyMs,
                    false
            );
        } catch (JsonProcessingException exception) {
            throw new BusinessException("CONFLICT",
                    "OpenAI-compatible 响应解析失败: " + exception.getMessage(), exception);
        }
    }

    /**
     * 执行parse工具Call。
     *
     * @param toolCallNode 工具CallNode
     * @return 执行结果
     */
    private LlmToolCall parseToolCall(JsonNode toolCallNode) {
        String id = toolCallNode.path("id").asText("");
        String name = toolCallNode.path("function").path("name").asText("");
        if (!StringUtils.hasText(id)) {
            throw new BusinessException("CONFLICT", "OpenAI-compatible 响应中缺少 tool_calls.id");
        }
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("CONFLICT", "OpenAI-compatible 响应中缺少 tool_calls.function.name");
        }
        return new LlmToolCall(
                id,
                name,
                toolCallNode.path("function").path("arguments").asText("{}")
        );
    }

    /**
     * 执行parse响应。
     *
     * @param responseBody 响应Body
     * @param latencyMs latencyMs
     * @return 执行结果
     */
    private ChatCompletionResult parseResponse(String responseBody, long latencyMs) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode answerNode = rootNode.path("choices").path(0).path("message").path("content");
            if (!answerNode.isTextual()) {
                throw new BusinessException("CONFLICT", "OpenAI-compatible 响应中缺少 message.content");
            }
            LlmTokenUsage tokenUsage = OpenAiCompatibleUsageParser.parseUsage(rootNode);
            return new ChatCompletionResult(answerNode.asText(), tokenUsage, latencyMs);
        } catch (JsonProcessingException exception) {
            throw new BusinessException("CONFLICT",
                    "OpenAI-compatible 响应解析失败: " + exception.getMessage(), exception);
        }
    }

    /**
     * Chat Completions 响应解析结果。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record ChatCompletionResult(String answer, LlmTokenUsage tokenUsage, long latencyMs) {
    }
}
