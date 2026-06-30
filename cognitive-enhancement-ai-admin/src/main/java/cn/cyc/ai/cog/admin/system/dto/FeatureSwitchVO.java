package cn.cyc.ai.cog.admin.system.dto;

import lombok.Data;

/**
 * FeatureSwitch视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class FeatureSwitchVO {

    /** 主键 ID */
    private Long id;
    /** feature键。 */
    private String featureKey;
    /** feature名称。 */
    private String featureName;
    /** segment。 */
    private String segment;
    /** 是否启用。 */
    private Boolean enabled;
    /** grayRuleJSON。 */
    private String grayRuleJson;
}
