package cn.cyc.ai.cog.admin.org.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * OrgMember视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class OrgMemberVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** orgID */
    private Long orgId;
    /** 用户 ID */
    private Long userId;
    /** deptID */
    private Long deptId;
    /** org角色。 */
    private String orgRole;
    /** 状态。 */
    private String status;
    /** joinedAt。 */
    private LocalDateTime joinedAt;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
