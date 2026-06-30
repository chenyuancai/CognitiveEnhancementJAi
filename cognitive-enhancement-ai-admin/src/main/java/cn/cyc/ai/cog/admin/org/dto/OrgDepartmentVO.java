package cn.cyc.ai.cog.admin.org.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * OrgDepartment视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class OrgDepartmentVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** orgID */
    private Long orgId;
    /** parentID */
    private Long parentId;
    /** dept名称。 */
    private String deptName;
    /** sortNo。 */
    private Integer sortNo;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
