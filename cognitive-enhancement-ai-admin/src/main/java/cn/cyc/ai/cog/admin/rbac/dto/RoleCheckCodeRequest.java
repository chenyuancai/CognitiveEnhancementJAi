package cn.cyc.ai.cog.admin.rbac.dto;

import lombok.Data;

/**
 * 角色编码唯一性校验请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class RoleCheckCodeRequest {

    /** 编码。 */
    private String code;

    /** excludeID */
    private Long excludeId;
}
