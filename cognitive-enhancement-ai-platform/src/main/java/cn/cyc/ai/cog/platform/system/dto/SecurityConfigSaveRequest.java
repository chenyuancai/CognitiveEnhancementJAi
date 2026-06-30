package cn.cyc.ai.cog.platform.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Security配置Save请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class SecurityConfigSaveRequest {

    /** 主键 ID */
    private Long id;

    /** 配置键。 */
    @NotBlank
    private String configKey;
    /** 配置值。 */
    @NotBlank
    private String configValue;
    /** 描述。 */
    private String description;
}
