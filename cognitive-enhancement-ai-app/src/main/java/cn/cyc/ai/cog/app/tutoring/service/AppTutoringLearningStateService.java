package cn.cyc.ai.cog.app.tutoring.service;

import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringCachedMessage;
import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringLearningStateCache;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyDecision;
import cn.cyc.ai.cog.platform.tutoring.service.TutoringPersistenceService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * 学习状态分析与持久化服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppTutoringLearningStateService {

    /**
     * 学习状态缓存。
     */
    private final AppTutoringLearningStateCache learningStateCache;

    /**
     * 学习辅导持久化服务提供者。
     */
    private final ObjectProvider<TutoringPersistenceService> tutoringPersistenceServiceProvider;

    /**
     * 创建学习状态服务。
     *
     * @param learningStateCache                 学习状态缓存
     * @param tutoringPersistenceServiceProvider 学习辅导持久化服务提供者
     */
    public AppTutoringLearningStateService(AppTutoringLearningStateCache learningStateCache,
                                             ObjectProvider<TutoringPersistenceService> tutoringPersistenceServiceProvider) {
        this.learningStateCache = learningStateCache;
        this.tutoringPersistenceServiceProvider = tutoringPersistenceServiceProvider;
    }

    /**
     * 加载指定会话的学生学习状态。
     *
     * @param sessionId 会话 ID
     * @return 学习状态，不存在时返回默认状态
     */
    public AppTutoringStudentState load(String sessionId) {
        return learningStateCache.loadSafely(sessionId).orElseGet(this::defaultState);
    }

    /**
     * 结合本轮消息更新学习状态，供策略决策使用。
     *
     * @param sessionId      会话 ID
     * @param message        当前用户消息
     * @param recentMessages 最近缓存消息
     * @return 更新后的学习状态
     */
    public AppTutoringStudentState prepareForDecision(String sessionId,
                                                      String message,
                                                      List<AppTutoringCachedMessage> recentMessages) {
        AppTutoringStudentState state = load(sessionId);
        if (isLikelyStuck(message, recentMessages)) {
            state.setStuckCount(state.getStuckCount() + 1);
            state.setConfusion(abbreviate(message));
        } else if (showsUnderstanding(message)) {
            state.setStuckCount(0);
            state.setMasteryLevel("IMPROVING");
        }
        if (!StringUtils.hasText(state.getKnowledgePoint()) && StringUtils.hasText(message)) {
            state.setKnowledgePoint(abbreviate(message));
        }
        return state;
    }

    /**
     * 持久化学习状态快照并写入缓存。
     *
     * @param sessionId 会话 ID
     * @param traceId   链路追踪 ID
     * @param state     学生当前学习状态
     * @param decision  教学策略决策
     */
    public void save(String sessionId, String traceId, AppTutoringStudentState state,
                     AppTutoringStrategyDecision decision) {
        state.setMasteryLevel(resolveMastery(state, decision));
        learningStateCache.saveSafely(sessionId, state);
        TutoringPersistenceService persistence = tutoringPersistenceServiceProvider.getIfAvailable();
        if (persistence != null) {
            persistence.saveLearningStateSnapshot(sessionId, traceId, state);
        }
    }

    /**
     * 创建默认学习状态。
     *
     * @return 初始学习状态
     */
    private AppTutoringStudentState defaultState() {
        AppTutoringStudentState state = new AppTutoringStudentState();
        state.setKnowledgePoint("UNKNOWN");
        state.setMasteryLevel("UNKNOWN");
        state.setConfusion("");
        state.setStuckCount(0);
        return state;
    }

    /**
     * 判断学生是否可能在当前轮次卡住。
     *
     * @param message        当前用户消息
     * @param recentMessages 最近缓存消息
     * @return 是否疑似卡住
     */
    private boolean isLikelyStuck(String message, List<AppTutoringCachedMessage> recentMessages) {
        String normalized = message == null ? "" : message.trim().toLowerCase(Locale.ROOT);
        if (!containsAny(normalized, "不懂", "不会", "没思路", "还是不明白", "卡住", "不会做")) {
            return false;
        }
        if (recentMessages.size() < 2) {
            return false;
        }
        AppTutoringCachedMessage prior = recentMessages.get(recentMessages.size() - 2);
        return "ASSISTANT".equalsIgnoreCase(prior.role());
    }

    /**
     * 判断学生消息是否表达已理解。
     *
     * @param message 当前用户消息
     * @return 是否表达理解
     */
    private boolean showsUnderstanding(String message) {
        String normalized = message == null ? "" : message.trim().toLowerCase(Locale.ROOT);
        return containsAny(normalized, "我明白了", "懂了", "知道了", "原来如此", "我理解了");
    }

    /**
     * 根据卡住次数与策略决策解析掌握度等级。
     *
     * @param state    学生当前学习状态
     * @param decision 教学策略决策
     * @return 掌握度等级
     */
    private String resolveMastery(AppTutoringStudentState state, AppTutoringStrategyDecision decision) {
        if (state.getStuckCount() >= 2) {
            return "NEEDS_STEP_BY_STEP";
        }
        if (decision.strategy().name().equals("DIRECT_ANSWER")) {
            return "FACT_CONFIRMED";
        }
        return state.getMasteryLevel();
    }

    /**
     * 执行containsAny。
     *
     * @param value 值
     * @param keywords keywords
     * @return 执行结果
     */
    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行abbreviate。
     *
     * @param text text
     * @return 执行结果
     */
    private String abbreviate(String text) {
        String compact = text.trim();
        return compact.length() <= 40 ? compact : compact.substring(0, 40);
    }
}
