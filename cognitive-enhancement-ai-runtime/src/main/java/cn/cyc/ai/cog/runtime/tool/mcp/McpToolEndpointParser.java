package cn.cyc.ai.cog.runtime.tool.mcp;

import cn.cyc.ai.cog.core.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 解析 Tool implRef 中的 MCP 端点配置。
 *
 * @author cyc
 */
public final class McpToolEndpointParser {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private McpToolEndpointParser() {
    }

    /**
     * MCP 端点配置。
     *
     * @param server   MCP Server 引用
     * @param toolName MCP 工具名称
     */
    public record McpEndpoint(String server, String toolName) {
    }

    /**
     * 解析 implRef。
     * <p>
     * 支持 JSON：{@code {"server":"local","tool":"demoEcho"}}
     *
     * @param implRef 实现引用
     * @return MCP 端点
     */
    public static McpEndpoint parse(String implRef) {
        if (implRef == null || implRef.isBlank()) {
            throw new BusinessException("INVALID_ARGUMENT", "MCP Tool implRef 不能为空");
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(implRef.trim());
            String server = node.path("server").asText(null);
            String tool = node.path("tool").asText(null);
            if (server == null || server.isBlank()) {
                throw new BusinessException("INVALID_ARGUMENT", "MCP Tool implRef 缺少 server");
            }
            if (tool == null || tool.isBlank()) {
                throw new BusinessException("INVALID_ARGUMENT", "MCP Tool implRef 缺少 tool");
            }
            return new McpEndpoint(server, tool);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("INVALID_ARGUMENT", "MCP Tool implRef 格式无效: " + implRef);
        }
    }
}
