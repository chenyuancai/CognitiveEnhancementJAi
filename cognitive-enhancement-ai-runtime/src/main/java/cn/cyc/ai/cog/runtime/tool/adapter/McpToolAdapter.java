package cn.cyc.ai.cog.runtime.tool.adapter;

import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapter;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapterContext;
import cn.cyc.ai.cog.runtime.tool.mcp.McpToolEndpointParser;
import cn.cyc.ai.cog.runtime.tool.spi.McpToolClient;
import org.springframework.stereotype.Component;

/**
 * MCP Tool Adapter。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class McpToolAdapter implements ToolAdapter {

    /** mcp工具客户端。 */
    private final McpToolClient mcpToolClient;

    /**
     * 创建McpToolAdapter。
     *
     * @param mcpToolClient mcp工具客户端
     */
    public McpToolAdapter(McpToolClient mcpToolClient) {
        this.mcpToolClient = mcpToolClient;
    }

    /**
     * 执行protocol类型。
     * @return 执行结果
     */
    @Override
    public ToolProtocolType protocolType() {
        return ToolProtocolType.MCP;
    }

    /**
     * 执行操作。
     *
     * @param context 上下文
     * @return 执行结果
     */
    @Override
    public Object invoke(ToolAdapterContext context) {
        ToolDefinition tool = context.tool();
        McpToolEndpointParser.McpEndpoint endpoint = McpToolEndpointParser.parse(tool.implRef());
        return mcpToolClient.callTool(
                endpoint.server(),
                endpoint.toolName(),
                context.request().input(),
                context.request().parameters()
        );
    }
}
