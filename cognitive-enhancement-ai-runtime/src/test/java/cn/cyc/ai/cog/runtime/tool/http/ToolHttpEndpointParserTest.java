package cn.cyc.ai.cog.runtime.tool.http;

import cn.cyc.ai.cog.runtime.tool.http.ToolHttpEndpointParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolHttpEndpointParserTest {

    @Test
    void shouldDefaultPostForPlainUrl() {
        ToolHttpEndpointParser.HttpEndpoint endpoint =
                ToolHttpEndpointParser.parse("https://example.com/tool/echo");

        assertEquals("https://example.com/tool/echo", endpoint.url());
        assertEquals("POST", endpoint.method());
    }

    @Test
    void shouldReadMethodFromJsonConfig() {
        ToolHttpEndpointParser.HttpEndpoint endpoint = ToolHttpEndpointParser.parse("""
                {"url":"https://example.com/search","method":"GET"}
                """);

        assertEquals("https://example.com/search", endpoint.url());
        assertEquals("GET", endpoint.method());
    }
}
