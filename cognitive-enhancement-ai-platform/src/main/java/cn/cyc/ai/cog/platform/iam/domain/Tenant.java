package cn.cyc.ai.cog.platform.iam.domain;

import java.time.LocalDateTime;

/**
 * 租户领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record Tenant(
        Long id,
        String tenantCode,
        String tenantName,
        String segment,
        String status,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
