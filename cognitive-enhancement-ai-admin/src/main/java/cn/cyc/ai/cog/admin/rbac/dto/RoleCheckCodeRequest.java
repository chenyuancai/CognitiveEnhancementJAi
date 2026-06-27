package cn.cyc.ai.cog.admin.rbac.dto;

import lombok.Data;

/**
 * 角色编码唯一性校验请求。
 */
@Data
public class RoleCheckCodeRequest {

    private String code;

    private Long excludeId;
}
