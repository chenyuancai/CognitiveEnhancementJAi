package cn.cyc.ai.cog.runtime.tool.mcp;

import cn.cyc.ai.cog.runtime.api.ToolHttpRequest;
import cn.cyc.ai.cog.runtime.api.ToolHttpResponse;
import cn.cyc.ai.cog.runtime.tool.spi.ToolHttpExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HttpMcpToolClientTest {

    @Test
    void shouldDetectHttpServerRef() {
        assertTrue(HttpMcpToolClient.isHttpServer("http://127.0.0.1:9090/mcp"));
        assertTrue(HttpMcpToolClient.isHttpServer("HTTPS://example.com/mcp"));
        assertFalse(HttpMcpToolClient.isHttpServer("local"));
    }

    @Test
    void shouldCallExternalMcpServerViaJsonRpc() {
        ToolHttpExecutor toolHttpExecutor = mock(ToolHttpExecutor.class);
        ObjectMapper objectMapper = new ObjectMapper();
        when(toolHttpExecutor.execute(any(ToolHttpRequest.class))).thenReturn(new ToolHttpResponse(
                200,
                """
                        {
                          "jsonrpc": "2.0",
                          "id": "1",
                          "result": {
                            "content": [{"type": "text", "text": "ok"}],
                            "isError": false
                          }
                        }
                        """,
                15
        ));

        HttpMcpToolClient client = new HttpMcpToolClient(toolHttpExecutor, objectMapper);
        Object payload = client.callTool(
                "http://127.0.0.1:9090/mcp",
                "search",
                Map.of("query", "hello"),
                Map.of()
        );

        ArgumentCaptor<ToolHttpRequest> captor = ArgumentCaptor.forClass(ToolHttpRequest.class);
        verify(toolHttpExecutor).execute(captor.capture());
        assertEquals("POST", captor.getValue().method());
        assertEquals("http://127.0.0.1:9090/mcp", captor.getValue().url());
        assertTrue(captor.getValue().body().contains("\"method\":\"tools/call\""));
        assertTrue(captor.getValue().body().contains("\"name\":\"search\""));

        Map<?, ?> result = (Map<?, ?>) payload;
        assertEquals("MCP", result.get("protocol"));
        assertEquals("search", result.get("tool"));
        assertEquals("http://127.0.0.1:9090/mcp", result.get("server"));
    }
}
