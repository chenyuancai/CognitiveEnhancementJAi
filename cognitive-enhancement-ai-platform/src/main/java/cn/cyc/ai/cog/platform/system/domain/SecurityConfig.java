package cn.cyc.ai.cog.platform.system.domain;

/**
 * Security配置
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record SecurityConfig(
        Long id,
        String configKey,
        String configValue,
        String description
) {
}
