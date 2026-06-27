package cn.cyc.ai.cog.platform.iam.domain;

import java.time.LocalDateTime;

/**
 * 租户领域对象。
 *
 * @param id         租户 ID
 * @param tenantCode 租户编码
 * @param tenantName 租户名称
 * @param segment    业务分段（2C/2B/2G）
 * @param status     状态
 * @param createTime 创建时间
 * @param updateTime 更新时间
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
