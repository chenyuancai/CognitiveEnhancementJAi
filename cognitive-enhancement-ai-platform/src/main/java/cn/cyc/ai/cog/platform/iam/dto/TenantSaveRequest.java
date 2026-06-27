package cn.cyc.ai.cog.platform.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 租户创建/更新请求。
 */
@Data
public class TenantSaveRequest {

    private Long id;

    @NotBlank(message = "租户编码不能为空")
    @Pattern(regexp = "^[a-z][a-z0-9._-]{2,62}$", message = "租户编码格式不合法")
    private String tenantCode;

    @NotBlank(message = "租户名称不能为空")
    private String tenantName;

    @NotBlank(message = "业务分段不能为空")
    private String segment;

    private String status;
}
