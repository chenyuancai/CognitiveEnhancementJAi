package cn.cyc.ai.cog.platform.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeatureSwitchSaveRequest {

    private Long id;

    @NotBlank
    private String featureKey;
    @NotBlank
    private String featureName;
    private String segment;
    private Boolean enabled;
    private String grayRuleJson;
}
