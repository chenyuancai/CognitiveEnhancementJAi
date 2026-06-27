package cn.cyc.ai.cog.admin.rbac.dto;

import lombok.Data;

/**
 * 权限码唯一性校验请求。
 */
@Data
public class PermissionCheckCodeRequest {

    private String code;

    private String scope;

    private Long excludeId;
}
