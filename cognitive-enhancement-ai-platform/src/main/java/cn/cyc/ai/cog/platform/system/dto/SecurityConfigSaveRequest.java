package cn.cyc.ai.cog.platform.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SecurityConfigSaveRequest {

    private Long id;

    @NotBlank
    private String configKey;
    @NotBlank
    private String configValue;
    private String description;
}
