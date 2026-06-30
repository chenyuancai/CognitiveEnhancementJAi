package cn.cyc.ai.cog.platform.org.domain;

import java.time.LocalDateTime;

/**
 * 组织成员领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record OrgMember(
        Long id,
        Long tenantId,
        Long orgId,
        Long userId,
        Long deptId,
        String orgRole,
        String status,
        LocalDateTime joinedAt,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
