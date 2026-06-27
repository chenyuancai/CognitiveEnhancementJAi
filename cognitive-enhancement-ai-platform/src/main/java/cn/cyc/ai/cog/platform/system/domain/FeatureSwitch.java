package cn.cyc.ai.cog.platform.system.domain;

public record FeatureSwitch(
        Long id,
        String featureKey,
        String featureName,
        String segment,
        Boolean enabled,
        String grayRuleJson
) {
}
