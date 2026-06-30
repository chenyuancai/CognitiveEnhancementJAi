package cn.cyc.ai.cog.app.tutoring.profile;

import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringProfileCache;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyDecision;
import cn.cyc.ai.cog.platform.tutoring.service.TutoringPersistenceService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * 用户学习画像刷新与持久化组件。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringProfileUpdater {

    /** 学习画像加载器。 */
    private final AppTutoringProfileLoader profileLoader;

    /** 学习画像 Redis 缓存。 */
    private final AppTutoringProfileCache profileCache;

    /** 掌握度聚合器。 */
    private final AppTutoringMasteryAggregator masteryAggregator;

    /** 辅导持久化服务提供者。 */
    private final ObjectProvider<TutoringPersistenceService> persistenceProvider;

    /**
     * 构造学习画像更新器。
     *
     * @param profileLoader       学习画像加载器
     * @param profileCache        学习画像缓存
     * @param masteryAggregator   掌握度聚合器
     * @param persistenceProvider 辅导持久化服务提供者
     */
    public AppTutoringProfileUpdater(AppTutoringProfileLoader profileLoader,
                                     AppTutoringProfileCache profileCache,
                                     AppTutoringMasteryAggregator masteryAggregator,
                                     ObjectProvider<TutoringPersistenceService> persistenceProvider) {
        this.profileLoader = profileLoader;
        this.profileCache = profileCache;
        this.masteryAggregator = masteryAggregator;
        this.persistenceProvider = persistenceProvider;
    }

    /**
     * 根据本轮学习状态与策略决策刷新用户画像。
     *
     * @param userId   用户 ID
     * @param state    学生学习状态
     * @param decision 策略决策结果
     */
    public void refresh(Long userId,
                        AppTutoringStudentState state,
                        AppTutoringStrategyDecision decision) {
        if (userId == null) {
            return;
        }
        AppLearningProfile profile = profileLoader.load(userId);
        profile.setUserId(userId);
        masteryAggregator.refreshProfile(profile, state, decision);
        persist(userId, profile);
    }

    /**
     * 更新用户画像中的活跃学习计划 ID。
     *
     * @param userId 用户 ID
     * @param planId 学习计划 ID
     */
    public void updateActivePlan(Long userId, Long planId) {
        if (userId == null || planId == null) {
            return;
        }
        AppLearningProfile profile = profileLoader.load(userId);
        profile.setUserId(userId);
        profile.setActivePlanId(planId);
        persist(userId, profile);
    }

    /**
     * 将画像同步写入 Redis 缓存与数据库。
     *
     * @param userId  用户 ID
     * @param profile 学习画像
     */
    private void persist(Long userId, AppLearningProfile profile) {
        profileCache.saveSafely(userId, profile);
        TutoringPersistenceService persistence = persistenceProvider.getIfAvailable();
        if (persistence != null) {
            persistence.upsertLearningProfile(userId, profile);
        }
    }
}
