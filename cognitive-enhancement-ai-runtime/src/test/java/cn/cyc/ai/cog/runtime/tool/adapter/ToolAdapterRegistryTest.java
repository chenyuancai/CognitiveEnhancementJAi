package cn.cyc.ai.cog.runtime.tool.adapter;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ToolAdapterRegistryTest {

    @Test
    void shouldResolveAdapterByProtocolType() {
        ToolAdapter httpAdapter = mock(ToolAdapter.class);
        when(httpAdapter.protocolType()).thenReturn(ToolProtocolType.HTTP);

        ToolAdapterRegistry registry = new ToolAdapterRegistry(List.of(httpAdapter));

        assertEquals(httpAdapter, registry.get(ToolProtocolType.HTTP));
    }

    @Test
    void shouldRejectUnknownProtocolType() {
        ToolAdapterRegistry registry = new ToolAdapterRegistry(List.of());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> registry.get(ToolProtocolType.MCP));

        assertEquals("CONFLICT", exception.getSemanticCode());
    }
}
