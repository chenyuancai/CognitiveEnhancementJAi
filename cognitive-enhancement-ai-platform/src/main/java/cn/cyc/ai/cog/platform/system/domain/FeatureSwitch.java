package cn.cyc.ai.cog.platform.system.domain;

/**
 * FeatureSwitch 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record FeatureSwitch(
        Long id,
        String featureKey,
        String featureName,
        String segment,
        Boolean enabled,
        String grayRuleJson
) {
}
