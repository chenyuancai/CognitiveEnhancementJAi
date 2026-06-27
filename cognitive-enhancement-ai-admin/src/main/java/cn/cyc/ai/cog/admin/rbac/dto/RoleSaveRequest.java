package cn.cyc.ai.cog.admin.rbac.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 角色新增/编辑请求。
 *
 * @author cyc
 */
@Data
public class RoleSaveRequest {

    private Long id;

    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    private String description;

    private String status;

    /** 前端展示色：purple/blue/green/amber/gray。 */
    private String avatarColor;
}
