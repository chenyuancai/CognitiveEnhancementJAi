package cn.cyc.ai.cog.platform.org.domain;

import java.time.LocalDateTime;

/**
 * 组织成员领域对象。
 *
 * @param id         成员 ID
 * @param tenantId   租户 ID
 * @param orgId      组织 ID
 * @param userId     用户 ID
 * @param deptId     部门 ID
 * @param orgRole    组织角色
 * @param status     状态
 * @param joinedAt   加入时间
 * @param createTime 创建时间
 * @param updateTime 更新时间
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
