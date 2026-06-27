package cn.cyc.ai.cog.platform.system.support;

import cn.cyc.ai.cog.platform.system.config.PlatformCacheProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 平台配置双级缓存：Caffeine L1 + 可选 Redis L2；写失效时广播 L1 失效。
 */
@Component
public class PlatformLocalCache {

    private static final String PREFIX_MARKER = "prefix:";

    private final PlatformCacheProperties properties;
    private final PlatformRedisCacheSupport redisCacheSupport;
    private final Cache<String, Object> localCache;
    private final boolean redisEnabled;

    public PlatformLocalCache(PlatformCacheProperties properties,
                              PlatformRedisCacheSupport redisCacheSupport) {
        this.properties = properties;
        this.redisCacheSupport = redisCacheSupport;
        this.redisEnabled = properties.isRedisEnabled();
        this.localCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(Math.max(1, properties.getTtlMinutes())))
                .maximumSize(2_000)
                .build();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type, Supplier<T> loader) {
        Object localHit = localCache.getIfPresent(key);
        if (localHit != null) {
            return type.cast(localHit);
        }
        if (redisEnabled) {
            Optional<T> redisHit = redisCacheSupport.get(redisKey(key), type);
            if (redisHit.isPresent()) {
                localCache.put(key, redisHit.get());
                return redisHit.get();
            }
        }
        T loaded = loader.get();
        localCache.put(key, loaded);
        if (redisEnabled && loaded != null) {
            redisCacheSupport.put(redisKey(key), loaded, ttl());
        }
        return loaded;
    }

    public Optional<String> getString(String key, Supplier<String> loader) {
        return Optional.ofNullable(get(key, String.class, loader));
    }

    public void invalidate(String key) {
        localCache.invalidate(key);
        if (redisEnabled) {
            redisCacheSupport.delete(redisKey(key));
            redisCacheSupport.publishInvalidation(properties.getInvalidationChannel(), key);
        }
    }

    public void invalidatePrefix(String prefix) {
        localCache.asMap().keySet().removeIf(key -> key.startsWith(prefix));
        if (redisEnabled) {
            redisCacheSupport.deleteByPrefix(redisKey(prefix));
            redisCacheSupport.publishInvalidation(properties.getInvalidationChannel(), PREFIX_MARKER + prefix);
        }
    }

    /**
     * Redis 失效广播回调：仅清理本节点 L1，避免循环广播。
     */
    public void onInvalidationMessage(String message) {
        if (!StringUtils.hasText(message)) {
            return;
        }
        if (message.startsWith(PREFIX_MARKER)) {
            String prefix = message.substring(PREFIX_MARKER.length());
            localCache.asMap().keySet().removeIf(key -> key.startsWith(prefix));
            return;
        }
        localCache.invalidate(message);
    }

    private Duration ttl() {
        return Duration.ofMinutes(Math.max(1, properties.getTtlMinutes()));
    }

    private String redisKey(String logicalKey) {
        return properties.getKeyPrefix() + logicalKey;
    }
}
