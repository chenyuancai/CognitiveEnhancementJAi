package cn.cyc.ai.cog.platform.system.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_sys_feature_switch")
public class FeatureSwitchEntity extends BaseEntity {

    private String featureKey;
    private String featureName;
    private String segment;
    private Boolean enabled;
    private String grayRuleJson;
}
