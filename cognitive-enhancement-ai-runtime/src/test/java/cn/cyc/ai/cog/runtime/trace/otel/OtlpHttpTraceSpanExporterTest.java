package cn.cyc.ai.cog.runtime.trace.otel;

import cn.cyc.ai.cog.runtime.api.ToolHttpRequest;
import cn.cyc.ai.cog.runtime.api.ToolHttpResponse;
import cn.cyc.ai.cog.runtime.tool.spi.ToolHttpExecutor;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpan;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanStatus;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OtlpHttpTraceSpanExporterTest {

    @Test
    void shouldExportSpanToOtlpEndpointWhenEnabled() throws Exception {
        OpenTelemetryTraceProperties properties = new OpenTelemetryTraceProperties();
        properties.setEnabled(true);
        properties.setEndpoint("http://127.0.0.1:4318/v1/traces");

        ToolHttpExecutor toolHttpExecutor = mock(ToolHttpExecutor.class);
        when(toolHttpExecutor.execute(any(ToolHttpRequest.class)))
                .thenReturn(new ToolHttpResponse(200, "{}", 5));

        OtlpHttpTraceSpanExporter exporter = new OtlpHttpTraceSpanExporter(
                properties, toolHttpExecutor, new ObjectMapper());

        TraceSpan span = new TraceSpan(
                "default",
                "trace-001",
                "span-001",
                null,
                TraceSpanType.TOOL,
                "tool.search",
                TraceSpanStatus.SUCCESS,
                12L,
                Map.of("protocol", "HTTP"),
                null,
                Instant.parse("2026-06-20T08:00:00.123Z")
        );
        exporter.onSpanExported(span);

        ArgumentCaptor<ToolHttpRequest> captor = ArgumentCaptor.forClass(ToolHttpRequest.class);
        verify(toolHttpExecutor).execute(captor.capture());
        assertEquals("POST", captor.getValue().method());
        assertEquals(properties.getEndpoint(), captor.getValue().url());
        assertTrue(captor.getValue().body().contains("\"traceId\":\"trace-001\""));
        assertTrue(captor.getValue().body().contains("\"spanId\":\"span-001\""));
    }
}
