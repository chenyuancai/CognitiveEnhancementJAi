package cn.cyc.ai.cog.runtime.audit.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 审计日志记录。
 *
 * @param tenantCode   租户编码
 * @param traceId      链路追踪 ID
 * @param eventType    事件类型
 * @param action       操作动作
 * @param resourceType 资源类型
 * @param resourceCode 资源编码
 * @param operator     操作人
 * @param success      是否成功
 * @param detail       审计详情
 * @param recordedAt   记录时间
 * @author cyc
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
