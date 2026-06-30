package cn.cyc.ai.cog.platform.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 平台配置缓存属性（Caffeine L1 + 可选 Redis L2）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.platform.cache")
public class PlatformCacheProperties {

    /** 是否启用 Redis L2（需 StringRedisTemplate）。 */
    private boolean redisEnabled = false;

    /** L1/L2 写入 TTL（分钟）。 */
    private int ttlMinutes = 5;

    /** Redis 键前缀。 */
    private String keyPrefix = "cog:platform:";

    /** L1 广播失效频道。 */
    private String invalidationChannel = "cog:platform:cache:invalidate";
}
