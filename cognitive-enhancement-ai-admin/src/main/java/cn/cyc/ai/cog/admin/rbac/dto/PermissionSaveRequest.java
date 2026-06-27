package cn.cyc.ai.cog.admin.rbac.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 自定义权限点新增请求。
 *
 * @author cyc
 */
@Data
public class PermissionSaveRequest {

    private Long id;

    @NotBlank
    private String scope;

    @NotBlank
    private String kind;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;
    private String moduleKey;
    private String groupKey;
    private String parentMenuKey;
    private String path;
    private String status;
    private Boolean builtin;
}
