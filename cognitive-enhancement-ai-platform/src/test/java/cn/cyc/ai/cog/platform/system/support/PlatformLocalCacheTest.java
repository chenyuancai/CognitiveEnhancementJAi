package cn.cyc.ai.cog.platform.system.support;

import cn.cyc.ai.cog.platform.system.config.PlatformCacheProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlatformLocalCacheTest {

    private PlatformLocalCache cache;

    @BeforeEach
    void setUp() {
        PlatformCacheProperties properties = new PlatformCacheProperties();
        properties.setRedisEnabled(false);
        cache = new PlatformLocalCache(properties, new DefaultNoopPlatformRedisCacheSupport());
    }

    @Test
    void shouldLoadOnceIntoLocalCache() {
        AtomicInteger loads = new AtomicInteger();
        String first = cache.get("sec:bool:demo", String.class, () -> {
            loads.incrementAndGet();
            return "true";
        });
        String second = cache.get("sec:bool:demo", String.class, () -> {
            loads.incrementAndGet();
            return "false";
        });
        assertEquals("true", first);
        assertEquals("true", second);
        assertEquals(1, loads.get());
    }

    @Test
    void shouldInvalidateLocalCacheEntry() {
        AtomicInteger loads = new AtomicInteger();
        cache.get("feature:demo", String.class, () -> {
            loads.incrementAndGet();
            return "on";
        });
        cache.invalidate("feature:demo");
        cache.get("feature:demo", String.class, () -> {
            loads.incrementAndGet();
            return "off";
        });
        assertEquals(2, loads.get());
    }

    @Test
    void shouldInvalidateByPrefix() {
        AtomicInteger loads = new AtomicInteger();
        cache.get("sec:bool:a", String.class, () -> {
            loads.incrementAndGet();
            return "1";
        });
        cache.get("sec:int:b", String.class, () -> {
            loads.incrementAndGet();
            return "2";
        });
        cache.invalidatePrefix("sec:");
        cache.get("sec:bool:a", String.class, () -> {
            loads.incrementAndGet();
            return "3";
        });
        cache.get("feature:x", String.class, () -> {
            loads.incrementAndGet();
            return "4";
        });
        assertEquals(4, loads.get());
    }
}
