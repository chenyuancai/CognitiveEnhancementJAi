package cn.cyc.ai.cog.runtime.tool.mcp;

import cn.cyc.ai.cog.runtime.tool.mcp.McpToolEndpointParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class McpToolEndpointParserTest {

    @Test
    void shouldReadServerAndToolFromJsonConfig() {
        McpToolEndpointParser.McpEndpoint endpoint = McpToolEndpointParser.parse("""
                {"server":"local","tool":"demoEcho"}
                """);

        assertEquals("local", endpoint.server());
        assertEquals("demoEcho", endpoint.toolName());
    }
}
