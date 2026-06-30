package cn.cyc.ai.cog.runtime.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ReAct 配置启用。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@EnableConfigurationProperties(ReActProperties.class)
public class ReActConfiguration {
}
