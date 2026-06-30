package cn.cyc.ai.cog.runtime.audit.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 审计日志记录。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AuditLogRecord(String tenantCode,
                             String traceId,
                             String eventType,
                             String action,
                             String resourceType,
                             String resourceCode,
                             String operator,
                             boolean success,
                             Map<String, Object> detail,
                             Instant recordedAt) {

    public AuditLogRecord {
        tenantCode = TenantContext.normalize(tenantCode);
        detail = sanitizeDetail(detail);
    }

    private static Map<String, Object> sanitizeDetail(Map<String, Object> detail) {
        if (detail == null || detail.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> sanitized = new LinkedHashMap<>();
        detail.forEach((key, value) -> {
            if (key != null && value != null) {
                sanitized.put(key, value);
            }
        });
        return Map.copyOf(sanitized);
    }
}
