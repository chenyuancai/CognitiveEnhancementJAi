package cn.cyc.ai.cog.runtime.tool.mcp;

import cn.cyc.ai.cog.core.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalMcpToolClientTest {

    private final LocalMcpToolClient client = new LocalMcpToolClient();

    @Test
    void callTool_demoEcho_shouldReturnEchoPayload() {
        Object result = client.callTool("local", "demoEcho", Map.of("question", "hello"), Map.of());

        assertEquals("MCP", ((Map<?, ?>) result).get("protocol"));
        assertEquals("hello", ((Map<?, ?>) ((Map<?, ?>) result).get("arguments")).get("question"));
    }

    @Test
    void callTool_unknownServer_shouldReject() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> client.callTool("remote", "demoEcho", Map.of(), Map.of()));
        assertEquals("CONFLICT", ex.getSemanticCode());
    }
}
