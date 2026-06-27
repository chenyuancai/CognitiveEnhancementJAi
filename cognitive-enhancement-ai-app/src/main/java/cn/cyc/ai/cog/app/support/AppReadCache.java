package cn.cyc.ai.cog.app.support;

import cn.cyc.ai.cog.app.config.AppReadCacheProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * C 端只读数据短 TTL 缓存。
 */
@Component
public class AppReadCache {

    private final AppReadCacheProperties properties;
    private final ConcurrentHashMap<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

    /**
     * @param properties 缓存配置
     */
    public AppReadCache(AppReadCacheProperties properties) {
        this.properties = properties;
    }

    /**
     * 读取缓存或加载。
     *
     * @param key    缓存键
     * @param loader 加载器
     * @param <T>    值类型
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Supplier<T> loader) {
        if (!properties.isEnabled()) {
            return loader.get();
        }
        long now = System.currentTimeMillis();
        CacheEntry<?> existing = cache.get(key);
        if (existing != null && !existing.isExpired(now, properties.getTtlSeconds())) {
            return (T) existing.value();
        }
        T loaded = loader.get();
        cache.put(key, new CacheEntry<>(loaded, now));
        return loaded;
    }

    private record CacheEntry<T>(T value, long cachedAtMillis) {

        boolean isExpired(long now, int ttlSeconds) {
            return now - cachedAtMillis > ttlSeconds * 1000L;
        }
    }
}
