package cn.cyc.ai.cog.admin.rbac.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 角色新增/编辑请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class RoleSaveRequest {

    /** 主键 ID */
    private Long id;

    /** 角色编码。 */
    private String roleCode;

    /** 角色名称。 */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /** 描述。 */
    private String description;

    /** 状态。 */
    private String status;

    /** 前端展示色：purple/blue/green/amber/gray。 */
    private String avatarColor;
}
