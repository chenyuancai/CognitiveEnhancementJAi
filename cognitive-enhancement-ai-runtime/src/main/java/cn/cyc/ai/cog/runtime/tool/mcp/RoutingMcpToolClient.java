package cn.cyc.ai.cog.runtime.tool.mcp;

import cn.cyc.ai.cog.runtime.tool.spi.McpToolClient;
import cn.cyc.ai.cog.runtime.tool.spi.ToolHttpExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 路由 MCP Tool 客户端：本地 server 走 {@link LocalMcpToolClient}，HTTP(S) server 走 {@link HttpMcpToolClient}。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class RoutingMcpToolClient implements McpToolClient {

    /** localMcp工具客户端。 */
    private final LocalMcpToolClient localMcpToolClient;
    /** httpMcp工具客户端。 */
    private final HttpMcpToolClient httpMcpToolClient;

    /**
     * 创建RoutingMcp工具客户端。
     *
     * @param toolHttpExecutor 工具HttpExecutor
     * @param objectMapper JSON 序列化器
     */
    @Autowired
    public RoutingMcpToolClient(ToolHttpExecutor toolHttpExecutor, ObjectMapper objectMapper) {
        this.localMcpToolClient = new LocalMcpToolClient();
        this.httpMcpToolClient = new HttpMcpToolClient(toolHttpExecutor, objectMapper);
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
        if (HttpMcpToolClient.isHttpServer(serverRef)) {
            return httpMcpToolClient.callTool(serverRef, toolName, arguments, parameters);
        }
        return localMcpToolClient.callTool(serverRef, toolName, arguments, parameters);
    }
}
