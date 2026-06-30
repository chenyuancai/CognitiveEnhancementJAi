package cn.cyc.ai.cog.platform.org.domain;

import java.time.LocalDateTime;

/**
 * 组织部门领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
