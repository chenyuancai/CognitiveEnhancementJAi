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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class RedisPlatformCacheSupport implements PlatformRedisCacheSupport {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(RedisPlatformCacheSupport.class);

    /** redisTemplate。 */
    private final StringRedisTemplate redisTemplate;
    /** 缓存ObjectMapper。 */
    private final ObjectMapper cacheObjectMapper;

    /**
     * 创建Redis平台缓存支持工具。
     *
     * @param redisTemplate redisTemplate
     * @param objectMapper JSON 序列化器
     */
    public RedisPlatformCacheSupport(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.cacheObjectMapper = objectMapper.copy();
        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();
        this.cacheObjectMapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    /**
     * 执行get。
     *
     * @param redisKey redis键
     * @param type 类型
     * @return 执行结果
     */
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

    /**
     * 执行put。
     *
     * @param redisKey redis键
     * @param value 值
     * @param ttl ttl
     */
    @Override
    public <T> void put(String redisKey, T value, Duration ttl) {
        try {
            String json = cacheObjectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(redisKey, json, ttl);
        } catch (Exception ex) {
            log.warn("写入 Redis 缓存失败，key={}", redisKey, ex);
        }
    }

    /**
     * 删除Item。
     *
     * @param redisKey redis键
     */
    @Override
    public void delete(String redisKey) {
        redisTemplate.delete(redisKey);
    }

    /**
     * 删除人Prefix。
     *
     * @param redisKeyPrefix redis键Prefix
     */
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

    /**
     * 执行publishInvalidation。
     *
     * @param channel channel
     * @param payload payload
     */
    @Override
    public void publishInvalidation(String channel, String payload) {
        try {
            redisTemplate.convertAndSend(channel, payload);
        } catch (Exception ex) {
            log.warn("广播缓存失效失败，payload={}", payload, ex);
        }
    }
}
