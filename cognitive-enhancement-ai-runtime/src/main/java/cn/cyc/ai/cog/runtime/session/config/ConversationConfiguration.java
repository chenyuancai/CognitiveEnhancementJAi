package cn.cyc.ai.cog.runtime.session.config;

import cn.cyc.ai.cog.runtime.session.service.ConversationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Runtime 会话上下文配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@EnableConfigurationProperties(ConversationProperties.class)
public class ConversationConfiguration {
}
