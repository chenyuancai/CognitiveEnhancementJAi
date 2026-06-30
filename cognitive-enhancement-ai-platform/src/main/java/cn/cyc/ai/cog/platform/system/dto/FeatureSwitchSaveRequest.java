package cn.cyc.ai.cog.platform.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * FeatureSwitchSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class FeatureSwitchSaveRequest {

    /** 主键 ID */
    private Long id;

    /** feature键。 */
    @NotBlank
    private String featureKey;
    /** feature名称。 */
    @NotBlank
    private String featureName;
    /** segment。 */
    private String segment;
    /** 是否启用。 */
    private Boolean enabled;
    /** grayRuleJSON。 */
    private String grayRuleJson;
}
