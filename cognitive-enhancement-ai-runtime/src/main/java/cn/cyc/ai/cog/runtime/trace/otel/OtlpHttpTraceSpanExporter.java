package cn.cyc.ai.cog.runtime.trace.otel;

import cn.cyc.ai.cog.runtime.api.ToolHttpRequest;
import cn.cyc.ai.cog.runtime.api.ToolHttpResponse;
import cn.cyc.ai.cog.runtime.tool.spi.ToolHttpExecutor;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpan;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将 TraceSpan 以 OTLP JSON 形式导出到外部 Collector。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@ConditionalOnProperty(prefix = "cog.runtime.trace.otel", name = "enabled", havingValue = "true")
public class OtlpHttpTraceSpanExporter implements TraceSpanExportListener {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(OtlpHttpTraceSpanExporter.class);

    /** properties。 */
    private final OpenTelemetryTraceProperties properties;
    /** 工具HttpExecutor。 */
    private final ToolHttpExecutor toolHttpExecutor;
    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    /**
     * 创建OtlpHttpTraceSpanExporter。
     */
    public OtlpHttpTraceSpanExporter(OpenTelemetryTraceProperties properties,
                                     ToolHttpExecutor toolHttpExecutor,
                                     ObjectMapper objectMapper) {
        this.properties = properties;
        this.toolHttpExecutor = toolHttpExecutor;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行onSpanExported。
     *
     * @param span span
     */
    @Override
    public void onSpanExported(TraceSpan span) {
        if (!properties.isEnabled()) {
            return;
        }
        try {
            String body = objectMapper.writeValueAsString(buildPayload(span));
            ToolHttpResponse response = toolHttpExecutor.execute(new ToolHttpRequest(
                    properties.getEndpoint(),
                    "POST",
                    Map.of("Content-Type", "application/json"),
                    body,
                    Duration.ofMillis(properties.getTimeoutMs())
            ));
            if (response.statusCode() >= 400) {
                log.warn("OTLP TraceSpan 导出失败, traceId={}, spanId={}, status={}, body={}",
                        span.traceId(), span.spanId(), response.statusCode(), abbreviate(response.body()));
            }
        } catch (Exception ex) {
            log.warn("OTLP TraceSpan 导出异常, traceId={}, spanId={}, reason={}",
                    span.traceId(), span.spanId(), ex.getMessage());
        }
    }

    private Map<String, Object> buildPayload(TraceSpan span) {
        Map<String, Object> otlpSpan = new LinkedHashMap<>();
        otlpSpan.put("traceId", span.traceId());
        otlpSpan.put("spanId", span.spanId());
        if (span.parentSpanId() != null && !span.parentSpanId().isBlank()) {
            otlpSpan.put("parentSpanId", span.parentSpanId());
        }
        otlpSpan.put("name", span.spanName());
        otlpSpan.put("kind", "INTERNAL");
        otlpSpan.put("startTimeUnixNano", span.recordedAt().minusMillis(span.latencyMs()).toEpochMilli() * 1_000_000L);
        otlpSpan.put("endTimeUnixNano", span.recordedAt().toEpochMilli() * 1_000_000L);
        otlpSpan.put("status", Map.of("code", span.status().name()));
        otlpSpan.put("attributes", toAttributes(span));

        Map<String, Object> scopeSpans = Map.of(
                "scope", Map.of("name", "cognitive-enhancement-ai"),
                "spans", List.of(otlpSpan)
        );
        Map<String, Object> resourceSpans = Map.of(
                "resource", Map.of("attributes", List.of(
                        attribute("service.name", "cognitive-enhancement-ai"),
                        attribute("tenant.code", span.tenantCode())
                )),
                "scopeSpans", List.of(scopeSpans)
        );
        return Map.of("resourceSpans", List.of(resourceSpans));
    }

    private List<Map<String, Object>> toAttributes(TraceSpan span) {
        List<Map<String, Object>> attributes = new ArrayList<>();
        attributes.add(attribute("span.type", span.spanType().name()));
        attributes.add(attribute("latency.ms", span.latencyMs()));
        span.attributes().forEach((key, value) -> attributes.add(attribute(key, String.valueOf(value))));
        if (span.errorStack() != null && !span.errorStack().isBlank()) {
            attributes.add(attribute("error.stack", span.errorStack()));
        }
        return attributes;
    }

    private Map<String, Object> attribute(String key, Object value) {
        Map<String, Object> attribute = new LinkedHashMap<>();
        attribute.put("key", key);
        attribute.put("value", Map.of("stringValue", String.valueOf(value)));
        return attribute;
    }

    /**
     * 执行abbreviate。
     *
     * @param body body
     * @return 执行结果
     */
    private String abbreviate(String body) {
        if (body == null) {
            return "";
        }
        return body.length() <= 200 ? body : body.substring(0, 200) + "...";
    }
}
