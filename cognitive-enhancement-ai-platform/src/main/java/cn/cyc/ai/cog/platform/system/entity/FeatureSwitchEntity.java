package cn.cyc.ai.cog.platform.system.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FeatureSwitch实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_sys_feature_switch")
public class FeatureSwitchEntity extends BaseEntity {

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
