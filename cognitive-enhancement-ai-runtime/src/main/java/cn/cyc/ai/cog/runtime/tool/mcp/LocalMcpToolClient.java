package cn.cyc.ai.cog.runtime.tool.mcp;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.tool.spi.McpToolClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 本地 MCP Tool 客户端骨架，用于演示与联调。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class LocalMcpToolClient implements McpToolClient {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(LocalMcpToolClient.class);

    private final Map<String, BiFunction<Object, Map<String, Object>, Object>> localTools = Map.of(
            "demoEcho", this::demoEcho
    );

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
        if (!"local".equalsIgnoreCase(serverRef)) {
            throw new BusinessException("CONFLICT", "暂不支持的 MCP server: " + serverRef);
        }
        BiFunction<Object, Map<String, Object>, Object> handler = localTools.get(toolName);
        if (handler == null) {
            throw new BusinessException("NOT_FOUND", "未找到 MCP Tool: " + toolName);
        }
        log.info("执行本地 MCP Tool, server={}, toolName={}", serverRef, toolName);
        return handler.apply(arguments, parameters == null ? Map.of() : parameters);
    }

    /**
     * 执行demoEcho。
     *
     * @param arguments arguments
     * @param parameters parameters
     * @return 执行结果
     */
    private Object demoEcho(Object arguments, Map<String, Object> parameters) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("protocol", "MCP");
        result.put("server", "local");
        result.put("tool", "demoEcho");
        result.put("arguments", arguments);
        result.put("parameters", parameters);
        result.put("message", "MCP demoEcho 已回显输入");
        return result;
    }
}
