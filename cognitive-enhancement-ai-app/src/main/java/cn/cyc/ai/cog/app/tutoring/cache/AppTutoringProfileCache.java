package cn.cyc.ai.cog.app.tutoring.cache;

import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.platform.system.support.PlatformRedisCacheSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * 用户学习画像 Redis 缓存。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringProfileCache {

    /** 日志记录器。 */
    private static final Logger log = LoggerFactory.getLogger(AppTutoringProfileCache.class);

    /** Redis 缓存支持组件。 */
    private final PlatformRedisCacheSupport redisCacheSupport;

    /** 学习辅导配置属性。 */
    private final AppTutoringProperties properties;

    /**
     * 构造学习画像缓存组件。
     *
     * @param redisCacheSupport Redis 缓存支持组件
     * @param properties        学习辅导配置属性
     */
    public AppTutoringProfileCache(PlatformRedisCacheSupport redisCacheSupport,
                                   AppTutoringProperties properties) {
        this.redisCacheSupport = redisCacheSupport;
        this.properties = properties;
    }

    /**
     * 安全加载用户学习画像，失败时返回空。
     *
     * @param userId 用户 ID
     * @return 学习画像
     */
    public Optional<AppLearningProfile> loadSafely(Long userId) {
        try {
            return redisCacheSupport.get(profileKey(userId), AppLearningProfile.class);
        } catch (Exception ex) {
            log.warn("tutoring profile load failed, userId={}", userId, ex);
            return Optional.empty();
        }
    }

    /**
     * 安全保存用户学习画像，失败时仅记录日志。
     *
     * @param userId  用户 ID
     * @param profile 学习画像
     */
    public void saveSafely(Long userId, AppLearningProfile profile) {
        try {
            redisCacheSupport.put(profileKey(userId), profile, Duration.ofDays(properties.getProfileTtlDays()));
        } catch (Exception ex) {
            log.warn("tutoring profile save failed, userId={}", userId, ex);
        }
    }

    /**
     * 生成学习画像 Redis 键。
     *
     * @param userId 用户 ID
     * @return Redis 键
     */
    static String profileKey(Long userId) {
        return "cog:learning:user:" + userId + ":profile";
    }
}
