package cn.cyc.ai.cog.platform.system.support;

import cn.cyc.ai.cog.platform.system.config.PlatformCacheProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlatformLocalCacheRedisL2Test {

    private final Map<String, String> redisStore = new ConcurrentHashMap<>();

    @BeforeEach
    void setUp() {
        redisStore.clear();
    }

    @Test
    void shouldPopulateRedisL2AndReuseAcrossLocalCacheInstances() {
        AtomicInteger loads = new AtomicInteger();
        Supplier<String> loader = () -> {
            loads.incrementAndGet();
            return "red";
        };

        PlatformCacheProperties properties = new PlatformCacheProperties();
        properties.setRedisEnabled(true);
        properties.setTtlMinutes(5);
        PlatformRedisCacheSupport redisSupport = new MapRedisCacheSupport(redisStore, new ObjectMapper());

        PlatformLocalCache firstNode = new PlatformLocalCache(properties, redisSupport);
        String first = firstNode.get("dict:color", String.class, loader);
        assertEquals("red", first);
        assertEquals(1, loads.get());

        PlatformLocalCache secondNode = new PlatformLocalCache(properties, redisSupport);
        String second = secondNode.get("dict:color", String.class, () -> {
            loads.incrementAndGet();
            return "blue";
        });
        assertEquals("red", second);
        assertEquals(1, loads.get());
    }

    private static final class MapRedisCacheSupport implements PlatformRedisCacheSupport {

        private final Map<String, String> store;
        private final ObjectMapper objectMapper;

        private MapRedisCacheSupport(Map<String, String> store, ObjectMapper objectMapper) {
            this.store = store;
            this.objectMapper = objectMapper;
        }

        @Override
        public <T> Optional<T> get(String redisKey, Class<T> type) {
            String json = store.get(redisKey);
            if (json == null) {
                return Optional.empty();
            }
            try {
                return Optional.of(objectMapper.readValue(json, type));
            } catch (Exception ex) {
                return Optional.empty();
            }
        }

        @Override
        public <T> void put(String redisKey, T value, Duration ttl) {
            try {
                store.put(redisKey, objectMapper.writeValueAsString(value));
            } catch (Exception ignored) {
            }
        }

        @Override
        public void delete(String redisKey) {
            store.remove(redisKey);
        }

        @Override
        public void deleteByPrefix(String redisKeyPrefix) {
            store.keySet().removeIf(key -> key.startsWith(redisKeyPrefix));
        }

        @Override
        public void publishInvalidation(String channel, String payload) {
        }
    }
}
