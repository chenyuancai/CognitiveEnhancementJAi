package cn.cyc.ai.cog.platform.system.config;

import cn.cyc.ai.cog.platform.system.support.PlatformLocalCache;
import cn.cyc.ai.cog.platform.system.support.RedisPlatformCacheSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 平台缓存 Redis L2 与失效广播（仅 Redis 启用且 classpath 可用时加载）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PlatformCacheProperties.class)
@ConditionalOnClass(RedisMessageListenerContainer.class)
@ConditionalOnProperty(prefix = "cog.platform.cache", name = "redis-enabled", havingValue = "true")
@ConditionalOnBean({RedisConnectionFactory.class, StringRedisTemplate.class})
public class PlatformCacheRedisConfiguration {

    @Bean
    RedisPlatformCacheSupport redisPlatformCacheSupport(StringRedisTemplate redisTemplate,
                                                        ObjectMapper objectMapper) {
        return new RedisPlatformCacheSupport(redisTemplate, objectMapper);
    }

    @Bean
    RedisMessageListenerContainer platformCacheInvalidationListenerContainer(
            RedisConnectionFactory connectionFactory,
            PlatformLocalCache platformLocalCache,
            PlatformCacheProperties properties) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        MessageListenerAdapter adapter = new MessageListenerAdapter(platformLocalCache, "onInvalidationMessage");
        container.addMessageListener(adapter, new PatternTopic(properties.getInvalidationChannel()));
        return container;
    }
}
