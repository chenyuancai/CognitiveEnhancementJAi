package cn.cyc.ai.cog.platform.org.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * OrgMemberSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class OrgMemberSaveRequest {

    /** 主键 ID */
    private Long id;

    /** orgID */
    private Long orgId;

    /** 用户 ID */
    @NotNull
    private Long userId;

    /** deptID */
    private Long deptId;
    /** org角色。 */
    private String orgRole = "MEMBER";
}
