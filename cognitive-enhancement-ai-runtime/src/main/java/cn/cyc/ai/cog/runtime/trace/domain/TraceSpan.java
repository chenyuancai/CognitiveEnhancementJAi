package cn.cyc.ai.cog.runtime.trace.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 统一调用流水 Span。
 *
 * @author cyc
 */
public record TraceSpan(
        String tenantCode,
        String traceId,
        String spanId,
        String parentSpanId,
        TraceSpanType spanType,
        String spanName,
        TraceSpanStatus status,
        long latencyMs,
        Map<String, Object> attributes,
        String errorStack,
        Instant recordedAt
) {

    public TraceSpan {
        tenantCode = TenantContext.normalize(tenantCode);
        if (attributes == null || attributes.isEmpty()) {
            attributes = Map.of();
        } else {
            Map<String, Object> copied = new LinkedHashMap<>();
            attributes.forEach((key, value) -> {
                if (key != null && value != null) {
                    copied.put(key, value);
                }
            });
            attributes = Map.copyOf(copied);
        }
    }
}
