package cn.cyc.ai.cog.platform.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 租户创建/更新请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class TenantSaveRequest {

    /** 主键 ID */
    private Long id;

    /** 租户编码。 */
    @NotBlank(message = "租户编码不能为空")
    @Pattern(regexp = "^[a-z][a-z0-9._-]{2,62}$", message = "租户编码格式不合法")
    private String tenantCode;

    /** 租户名称。 */
    @NotBlank(message = "租户名称不能为空")
    private String tenantName;

    /** segment。 */
    @NotBlank(message = "业务分段不能为空")
    private String segment;

    /** 状态。 */
    private String status;
}
