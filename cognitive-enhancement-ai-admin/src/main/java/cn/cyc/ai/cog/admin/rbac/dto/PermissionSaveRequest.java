package cn.cyc.ai.cog.admin.rbac.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 自定义权限点新增请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class PermissionSaveRequest {

    /** 主键 ID */
    private Long id;

    /** scope。 */
    @NotBlank
    private String scope;

    /** kind。 */
    @NotBlank
    private String kind;

    /** 编码。 */
    @NotBlank
    private String code;

    /** 名称。 */
    @NotBlank
    private String name;

    /** 描述。 */
    private String description;
    /** module键。 */
    private String moduleKey;
    /** group键。 */
    private String groupKey;
    /** parentMenu键。 */
    private String parentMenuKey;
    /** 路径。 */
    private String path;
    /** 状态。 */
    private String status;
    /** builtin。 */
    private Boolean builtin;
}
