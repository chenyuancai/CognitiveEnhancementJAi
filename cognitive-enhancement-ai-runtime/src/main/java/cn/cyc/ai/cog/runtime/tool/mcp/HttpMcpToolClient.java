package cn.cyc.ai.cog.runtime.tool.mcp;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.api.ToolHttpRequest;
import cn.cyc.ai.cog.runtime.api.ToolHttpResponse;
import cn.cyc.ai.cog.runtime.tool.spi.McpToolClient;
import cn.cyc.ai.cog.runtime.tool.spi.ToolHttpExecutor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 基于 HTTP JSON-RPC 的外部 MCP Server 客户端。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class HttpMcpToolClient implements McpToolClient {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(HttpMcpToolClient.class);

    /** 工具HttpExecutor。 */
    private final ToolHttpExecutor toolHttpExecutor;
    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;
    /** 默认Timeout。 */
    private final Duration defaultTimeout;

    /**
     * 创建HttpMcp工具客户端。
     *
     * @param toolHttpExecutor 工具HttpExecutor
     * @param objectMapper JSON 序列化器
     */
    public HttpMcpToolClient(ToolHttpExecutor toolHttpExecutor, ObjectMapper objectMapper) {
        this(toolHttpExecutor, objectMapper, Duration.ofSeconds(30));
    }

    /**
     * 创建HttpMcp工具客户端。
     *
     * @param toolHttpExecutor 工具HttpExecutor
     * @param objectMapper JSON 序列化器
     * @param defaultTimeout 默认Timeout
     */
    public HttpMcpToolClient(ToolHttpExecutor toolHttpExecutor, ObjectMapper objectMapper, Duration defaultTimeout) {
        this.toolHttpExecutor = toolHttpExecutor;
        this.objectMapper = objectMapper;
        this.defaultTimeout = defaultTimeout;
    }

    /**
     * 执行call工具。
     *
     * @param serverRef serverRef
     * @param toolName 工具名称
     * @param arguments arguments
     * @param parameters parameters
     * @return 执行结果
     */
    @Override
    public Object callTool(String serverRef, String toolName, Object arguments, Map<String, Object> parameters) {
        if (!isHttpServer(serverRef)) {
            throw new BusinessException("CONFLICT", "HTTP MCP 客户端不支持 server: " + serverRef);
        }
        String requestBody = buildRequestBody(toolName, arguments);
        log.info("调用外部 MCP Tool, server={}, toolName={}", serverRef, toolName);
        ToolHttpResponse response = toolHttpExecutor.execute(new ToolHttpRequest(
                serverRef,
                "POST",
                Map.of("Content-Type", "application/json"),
                requestBody,
                defaultTimeout
        ));
        if (response.statusCode() >= 400) {
            throw new BusinessException("CONFLICT",
                    "MCP Tool 调用失败, status=" + response.statusCode() + ", body=" + abbreviate(response.body()));
        }
        return parseResponse(serverRef, toolName, response.body());
    }

    static boolean isHttpServer(String serverRef) {
        if (serverRef == null || serverRef.isBlank()) {
            return false;
        }
        String normalized = serverRef.trim().toLowerCase();
        return normalized.startsWith("http://") || normalized.startsWith("https://");
    }

    /**
     * 构建请求Body。
     *
     * @param toolName 工具名称
     * @param arguments arguments
     * @return 构建结果
     */
    private String buildRequestBody(String toolName, Object arguments) {
        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("name", toolName);
            params.put("arguments", normalizeArguments(arguments));
            Map<String, Object> payload = Map.of(
                    "jsonrpc", "2.0",
                    "id", UUID.randomUUID().toString(),
                    "method", "tools/call",
                    "params", params
            );
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new BusinessException("INVALID_ARGUMENT", "MCP 请求序列化失败: " + ex.getMessage(), ex);
        }
    }

    /**
     * 执行normalizeArguments。
     *
     * @param arguments arguments
     * @return 执行结果
     */
    private Object normalizeArguments(Object arguments) {
        if (arguments == null) {
            return Map.of();
        }
        if (arguments instanceof Map<?, ?> map) {
            return map;
        }
        return Map.of("input", arguments);
    }

    /**
     * 执行parse响应。
     *
     * @param serverRef serverRef
     * @param toolName 工具名称
     * @param body body
     * @return 执行结果
     */
    private Object parseResponse(String serverRef, String toolName, String body) {
        if (body == null || body.isBlank()) {
            throw new BusinessException("CONFLICT", "MCP Tool 响应为空");
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode error = root.path("error");
            if (!error.isMissingNode() && !error.isNull()) {
                throw new BusinessException("CONFLICT",
                        "MCP Tool 返回错误: " + error.path("message").asText(error.toString()));
            }
            JsonNode result = root.path("result");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("protocol", "MCP");
            payload.put("server", serverRef);
            payload.put("tool", toolName);
            if (result.isMissingNode() || result.isNull()) {
                payload.put("rawBody", body);
                return payload;
            }
            payload.put("result", objectMapper.convertValue(result, Object.class));
            return payload;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("CONFLICT", "MCP Tool 响应解析失败: " + ex.getMessage(), ex);
        }
    }

    /**
     * 执行abbreviate。
     *
     * @param body body
     * @return 执行结果
     */
    private String abbreviate(String body) {
        if (body == null) {
            return "";
        }
        return body.length() <= 200 ? body : body.substring(0, 200) + "...";
    }
}
