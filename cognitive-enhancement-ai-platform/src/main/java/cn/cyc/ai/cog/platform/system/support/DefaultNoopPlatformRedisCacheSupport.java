package cn.cyc.ai.cog.platform.system.support;

import java.time.Duration;
import java.util.Optional;

/**
 * 无 Redis 依赖时的默认空实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class DefaultNoopPlatformRedisCacheSupport implements PlatformRedisCacheSupport {

    /**
     * 执行get。
     *
     * @param redisKey redis键
     * @param type 类型
     * @return 执行结果
     */
    @Override
    public <T> Optional<T> get(String redisKey, Class<T> type) {
        return Optional.empty();
    }

    /**
     * 执行put。
     *
     * @param redisKey redis键
     * @param value 值
     * @param ttl ttl
     */
    @Override
    public <T> void put(String redisKey, T value, Duration ttl) {
    }

    /**
     * 删除Item。
     *
     * @param redisKey redis键
     */
    @Override
    public void delete(String redisKey) {
    }

    /**
     * 删除人Prefix。
     *
     * @param redisKeyPrefix redis键Prefix
     */
    @Override
    public void deleteByPrefix(String redisKeyPrefix) {
    }

    /**
     * 执行publishInvalidation。
     *
     * @param channel channel
     * @param payload payload
     */
    @Override
    public void publishInvalidation(String channel, String payload) {
    }
}
