package cn.cyc.ai.cog.platform.system.domain;

import java.time.LocalDateTime;

/**
 * AuditLog 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AuditLog(
        Long id,
        Long tenantId,
        Long operatorId,
        String operatorName,
        String action,
        String message,
        String resourceType,
        String resourceId,
        String beforeJson,
        String afterJson,
        String ipAddress,
        LocalDateTime createTime
) {
}
