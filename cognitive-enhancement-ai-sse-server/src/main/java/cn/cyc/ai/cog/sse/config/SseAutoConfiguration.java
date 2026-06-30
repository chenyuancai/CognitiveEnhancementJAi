package cn.cyc.ai.cog.sse.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SseAuto配置
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@EnableConfigurationProperties(SseProperties.class)
public class SseAutoConfiguration {
}
