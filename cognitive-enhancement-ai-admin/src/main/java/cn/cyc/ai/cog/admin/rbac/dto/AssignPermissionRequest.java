package cn.cyc.ai.cog.admin.rbac.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色授权请求：支持按权限点编码整体覆盖绑定。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AssignPermissionRequest {

    /** 角色ID */
    private Long roleId;

    /** 权限点编码列表（前端契约）。 */
    private List<String> permissionCodes;

    /** 权限点 ID 列表（兼容旧契约）。 */
    private List<Long> permissionIds;
}
