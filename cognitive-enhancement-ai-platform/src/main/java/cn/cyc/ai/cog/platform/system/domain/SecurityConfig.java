package cn.cyc.ai.cog.platform.system.domain;

public record SecurityConfig(
        Long id,
        String configKey,
        String configValue,
        String description
) {
}
