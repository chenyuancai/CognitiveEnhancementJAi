package cn.cyc.ai.cog.platform.system.support;

import cn.cyc.ai.cog.platform.system.config.PlatformCacheProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Redis L2 实现（需 StringRedisTemplate）。
 */
public class RedisPlatformCacheSupport implements PlatformRedisCacheSupport {

    private static final Logger log = LoggerFactory.getLogger(RedisPlatformCacheSupport.class);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper cacheObjectMapper;

    public RedisPlatformCacheSupport(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.cacheObjectMapper = objectMapper.copy();
        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();
        this.cacheObjectMapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    @Override
    public <T> Optional<T> get(String redisKey, Class<T> type) {
        try {
            String json = redisTemplate.opsForValue().get(redisKey);
            if (!StringUtils.hasText(json)) {
                return Optional.empty();
            }
            return Optional.of(cacheObjectMapper.readValue(json, type));
        } catch (Exception ex) {
            log.warn("读取 Redis 缓存失败，key={}", redisKey, ex);
            redisTemplate.delete(redisKey);
            return Optional.empty();
        }
    }

    @Override
    public <T> void put(String redisKey, T value, Duration ttl) {
        try {
            String json = cacheObjectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(redisKey, json, ttl);
        } catch (Exception ex) {
            log.warn("写入 Redis 缓存失败，key={}", redisKey, ex);
        }
    }

    @Override
    public void delete(String redisKey) {
        redisTemplate.delete(redisKey);
    }

    @Override
    public void deleteByPrefix(String redisKeyPrefix) {
        String pattern = redisKeyPrefix + "*";
        AtomicInteger deleted = new AtomicInteger();
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                redisTemplate.delete(cursor.next());
                deleted.incrementAndGet();
            }
        } catch (Exception ex) {
            log.warn("按前缀清理 Redis 缓存失败，prefix={}", redisKeyPrefix, ex);
        }
        if (deleted.get() > 0) {
            log.debug("Redis 前缀失效删除 {} 项，prefix={}", deleted.get(), redisKeyPrefix);
        }
    }

    @Override
    public void publishInvalidation(String channel, String payload) {
        try {
            redisTemplate.convertAndSend(channel, payload);
        } catch (Exception ex) {
            log.warn("广播缓存失效失败，payload={}", payload, ex);
        }
    }
}
