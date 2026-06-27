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
 */
@Component
public class McpToolAdapter implements ToolAdapter {

    private final McpToolClient mcpToolClient;

    public McpToolAdapter(McpToolClient mcpToolClient) {
        this.mcpToolClient = mcpToolClient;
    }

    @Override
    public ToolProtocolType protocolType() {
        return ToolProtocolType.MCP;
    }

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
