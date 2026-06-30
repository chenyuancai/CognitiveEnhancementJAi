package cn.cyc.ai.cog.platform.system.config;

import cn.cyc.ai.cog.platform.system.support.DefaultNoopPlatformRedisCacheSupport;
import cn.cyc.ai.cog.platform.system.support.PlatformRedisCacheSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 平台缓存属性注册（L1 始终可用；Redis 见 {@link PlatformCacheRedisConfiguration}）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PlatformCacheProperties.class)
public class PlatformCacheConfiguration {

    @Bean
    @ConditionalOnMissingBean(PlatformRedisCacheSupport.class)
    PlatformRedisCacheSupport defaultPlatformRedisCacheSupport() {
        return new DefaultNoopPlatformRedisCacheSupport();
    }
}
