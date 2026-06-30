package cn.cyc.ai.cog.app.tutoring.service;

import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;
import cn.cyc.ai.cog.app.tutoring.profile.AppTutoringProfileLoader;
import cn.cyc.ai.cog.app.tutoring.profile.AppTutoringProfileUpdater;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyDecision;
import org.springframework.stereotype.Service;

/**
 * 用户学习画像门面服务，委托 Loader / Updater 完成读写。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppTutoringProfileService {

    /**
     * 学习画像加载器。
     */
    private final AppTutoringProfileLoader profileLoader;

    /**
     * 学习画像更新器。
     */
    private final AppTutoringProfileUpdater profileUpdater;

    /**
     * 创建学习画像门面服务。
     *
     * @param profileLoader  学习画像加载器
     * @param profileUpdater 学习画像更新器
     */
    public AppTutoringProfileService(AppTutoringProfileLoader profileLoader,
                                     AppTutoringProfileUpdater profileUpdater) {
        this.profileLoader = profileLoader;
        this.profileUpdater = profileUpdater;
    }

    /**
     * 加载当前登录用户的学习画像。
     *
     * @return 学习画像
     */
    public AppLearningProfile loadForCurrentUser() {
        return profileLoader.loadForCurrentUser();
    }

    /**
     * 按用户 ID 加载学习画像。
     *
     * @param userId 用户 ID
     * @return 学习画像
     */
    public AppLearningProfile load(Long userId) {
        return profileLoader.load(userId);
    }

    /**
     * 根据本轮学习状态与策略决策刷新用户画像。
     *
     * @param userId   用户 ID
     * @param state    学生当前学习状态
     * @param decision 教学策略决策
     */
    public void refresh(Long userId,
                        AppTutoringStudentState state,
                        AppTutoringStrategyDecision decision) {
        profileUpdater.refresh(userId, state, decision);
    }

    /**
     * 更新用户当前活跃的学习计划 ID。
     *
     * @param userId 用户 ID
     * @param planId 学习计划 ID
     */
    public void updateActivePlan(Long userId, Long planId) {
        profileUpdater.updateActivePlan(userId, planId);
    }
}
