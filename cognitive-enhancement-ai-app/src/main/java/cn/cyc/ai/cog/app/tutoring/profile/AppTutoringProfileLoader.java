package cn.cyc.ai.cog.app.tutoring.profile;

import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringProfileCache;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.strategy.AppMasteryLevel;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.platform.tutoring.service.TutoringPersistenceService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 用户学习画像加载器：Redis 优先，DB 回源。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringProfileLoader {

    /** 学习画像 Redis 缓存。 */
    private final AppTutoringProfileCache profileCache;

    /** 辅导持久化服务提供者。 */
    private final ObjectProvider<TutoringPersistenceService> persistenceProvider;

    /**
     * 构造学习画像加载器。
     *
     * @param profileCache        学习画像缓存
     * @param persistenceProvider 辅导持久化服务提供者
     */
    public AppTutoringProfileLoader(AppTutoringProfileCache profileCache,
                                    ObjectProvider<TutoringPersistenceService> persistenceProvider) {
        this.profileCache = profileCache;
        this.persistenceProvider = persistenceProvider;
    }

    /**
     * 加载当前登录用户的学习画像。
     *
     * @return 学习画像
     */
    public AppLearningProfile loadForCurrentUser() {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            return emptyProfile(null);
        }
        return load(userId);
    }

    /**
     * 按用户 ID 加载学习画像，缓存未命中时回源数据库。
     *
     * @param userId 用户 ID
     * @return 学习画像
     */
    public AppLearningProfile load(Long userId) {
        return profileCache.loadSafely(userId)
                .or(() -> {
                    TutoringPersistenceService persistence = persistenceProvider.getIfAvailable();
                    if (persistence == null) {
                        return java.util.Optional.empty();
                    }
                    AppLearningProfile dbProfile = persistence.findLearningProfile(userId, AppLearningProfile.class);
                    if (dbProfile != null) {
                        profileCache.saveSafely(userId, dbProfile);
                    }
                    return java.util.Optional.ofNullable(dbProfile);
                })
                .orElseGet(() -> emptyProfile(userId));
    }

    /**
     * 创建空的学习画像占位对象。
     *
     * @param userId 用户 ID
     * @return 空学习画像
     */
    private AppLearningProfile emptyProfile(Long userId) {
        AppLearningProfile profile = new AppLearningProfile();
        profile.setUserId(userId);
        profile.setOverallMastery(AppMasteryLevel.UNKNOWN);
        profile.setLastUpdatedAt(Instant.now());
        return profile;
    }
}
