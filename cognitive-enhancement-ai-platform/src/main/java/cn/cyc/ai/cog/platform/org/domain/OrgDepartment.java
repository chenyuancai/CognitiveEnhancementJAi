package cn.cyc.ai.cog.platform.org.domain;

import java.time.LocalDateTime;

/**
 * 组织部门领域对象。
 *
 * @param id         部门 ID
 * @param tenantId   租户 ID
 * @param orgId      组织 ID
 * @param parentId   父部门 ID
 * @param deptName   部门名称
 * @param sortNo     排序号
 * @param createTime 创建时间
 * @param updateTime 更新时间
 */
public record OrgDepartment(
        Long id,
        Long tenantId,
        Long orgId,
        Long parentId,
        String deptName,
        Integer sortNo,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
