package cn.cyc.ai.cog.platform.system.support;

import java.time.Duration;
import java.util.Optional;

/**
 * 平台缓存 Redis L2 与失效广播 SPI（可选实现）。
 */
public interface PlatformRedisCacheSupport {

    <T> Optional<T> get(String redisKey, Class<T> type);

    <T> void put(String redisKey, T value, Duration ttl);

    void delete(String redisKey);

    void deleteByPrefix(String redisKeyPrefix);

    void publishInvalidation(String channel, String payload);
}
