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
 */
@Component
public class RoutingMcpToolClient implements McpToolClient {

    private final LocalMcpToolClient localMcpToolClient;
    private final HttpMcpToolClient httpMcpToolClient;

    @Autowired
    public RoutingMcpToolClient(ToolHttpExecutor toolHttpExecutor, ObjectMapper objectMapper) {
        this.localMcpToolClient = new LocalMcpToolClient();
        this.httpMcpToolClient = new HttpMcpToolClient(toolHttpExecutor, objectMapper);
    }

    @Override
    public Object callTool(String serverRef, String toolName, Object arguments, Map<String, Object> parameters) {
        if (HttpMcpToolClient.isHttpServer(serverRef)) {
            return httpMcpToolClient.callTool(serverRef, toolName, arguments, parameters);
        }
        return localMcpToolClient.callTool(serverRef, toolName, arguments, parameters);
    }
}
