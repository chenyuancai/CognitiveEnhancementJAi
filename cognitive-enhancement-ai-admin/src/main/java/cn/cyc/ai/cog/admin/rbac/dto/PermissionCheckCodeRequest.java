package cn.cyc.ai.cog.admin.rbac.dto;

import lombok.Data;

/**
 * 权限码唯一性校验请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class PermissionCheckCodeRequest {

    /** 编码。 */
    private String code;

    /** scope。 */
    private String scope;

    /** excludeID */
    private Long excludeId;
}
