package cn.cyc.ai.cog.admin.system.dto;

import lombok.Data;

@Data
public class FeatureSwitchVO {

    private Long id;
    private String featureKey;
    private String featureName;
    private String segment;
    private Boolean enabled;
    private String grayRuleJson;
}
