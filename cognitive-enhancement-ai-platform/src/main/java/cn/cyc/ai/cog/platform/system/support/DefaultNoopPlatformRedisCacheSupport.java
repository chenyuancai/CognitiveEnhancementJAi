package cn.cyc.ai.cog.platform.system.support;

import java.time.Duration;
import java.util.Optional;

/**
 * 无 Redis 依赖时的默认空实现。
 */
public class DefaultNoopPlatformRedisCacheSupport implements PlatformRedisCacheSupport {

    @Override
    public <T> Optional<T> get(String redisKey, Class<T> type) {
        return Optional.empty();
    }

    @Override
    public <T> void put(String redisKey, T value, Duration ttl) {
    }

    @Override
    public void delete(String redisKey) {
    }

    @Override
    public void deleteByPrefix(String redisKeyPrefix) {
    }

    @Override
    public void publishInvalidation(String channel, String payload) {
    }
}
