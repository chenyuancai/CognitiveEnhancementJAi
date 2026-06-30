package cn.cyc.ai.cog.app.tutoring.profile;

import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;
import cn.cyc.ai.cog.app.tutoring.strategy.AppMasteryLevel;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTeachingStrategy;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyDecision;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 掌握度聚合器：合并会话学习状态与用户画像中的知识点掌握信息。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringMasteryAggregator {

    /**
     * 将画像中的补学标记合并到会话学习状态。
     *
     * @param sessionState 会话学习状态
     * @param profile      用户学习画像
     * @return 合并后的学习状态
     */
    public AppTutoringStudentState mergeSessionWithProfile(AppTutoringStudentState sessionState,
                                                           AppLearningProfile profile) {
        if (profile == null || sessionState == null || !StringUtils.hasText(sessionState.getKnowledgePoint())) {
            return sessionState;
        }
        AppLearningProfile.KnowledgePointMastery point = findPoint(profile, sessionState.getKnowledgePoint());
        if (point != null && point.getMastery() == AppMasteryLevel.NEEDS_REMEDIAL) {
            sessionState.setMasteryLevel(AppMasteryLevel.NEEDS_REMEDIAL.name());
        }
        return sessionState;
    }

    /**
     * 根据会话状态与策略决策解析当前掌握等级。
     *
     * @param state    学生学习状态
     * @param decision 策略决策结果
     * @return 掌握等级
     */
    public AppMasteryLevel resolveMastery(AppTutoringStudentState state, AppTutoringStrategyDecision decision) {
        if (state.getStuckCount() >= 2) {
            return AppMasteryLevel.NEEDS_REMEDIAL;
        }
        if (decision.strategy() == AppTeachingStrategy.PRACTICE_CHECK) {
            return AppMasteryLevel.PRACTICE_READY;
        }
        if (decision.strategy() == AppTeachingStrategy.DIRECT_ANSWER) {
            return AppMasteryLevel.IMPROVING;
        }
        String level = state.getMasteryLevel();
        if (!StringUtils.hasText(level)) {
            return AppMasteryLevel.UNKNOWN;
        }
        try {
            return AppMasteryLevel.valueOf(level.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return AppMasteryLevel.UNKNOWN;
        }
    }

    /**
     * 根据本轮决策刷新用户学习画像中的掌握度信息。
     *
     * @param profile  用户学习画像
     * @param state    学生学习状态
     * @param decision 策略决策结果
     */
    public void refreshProfile(AppLearningProfile profile,
                               AppTutoringStudentState state,
                               AppTutoringStrategyDecision decision) {
        if (profile == null || state == null) {
            return;
        }
        AppMasteryLevel mastery = resolveMastery(state, decision);
        state.setMasteryLevel(mastery.name());
        if (!StringUtils.hasText(state.getKnowledgePoint())) {
            profile.setOverallMastery(worst(profile.getOverallMastery(), mastery));
            profile.setLastUpdatedAt(Instant.now());
            refreshWeakTopics(profile);
            return;
        }
        AppLearningProfile.KnowledgePointMastery point = findOrCreate(profile, state.getKnowledgePoint());
        point.setStuckCount(state.getStuckCount());
        point.setMastery(worst(point.getMastery(), mastery));
        point.setLastStrategy(decision.strategy());
        profile.setOverallMastery(aggregateOverall(profile));
        profile.setLastUpdatedAt(Instant.now());
        refreshWeakTopics(profile);
    }

    /**
     * 根据各知识点掌握度刷新薄弱主题列表。
     *
     * @param profile 用户学习画像
     */
    private void refreshWeakTopics(AppLearningProfile profile) {
        List<String> weak = new ArrayList<>();
        for (AppLearningProfile.KnowledgePointMastery point : profile.getKnowledgePoints()) {
            if (point.getMastery() == AppMasteryLevel.NEEDS_REMEDIAL
                    || point.getMastery() == AppMasteryLevel.UNKNOWN) {
                weak.add(point.getName());
            }
        }
        profile.setWeakTopics(weak);
    }

    /**
     * 聚合所有知识点的整体掌握度（取最低等级）。
     *
     * @param profile 用户学习画像
     * @return 整体掌握等级
     */
    private AppMasteryLevel aggregateOverall(AppLearningProfile profile) {
        return profile.getKnowledgePoints().stream()
                .map(AppLearningProfile.KnowledgePointMastery::getMastery)
                .filter(level -> level != null && level != AppMasteryLevel.UNKNOWN)
                .min(java.util.Comparator.comparingInt(AppMasteryLevel::ordinal))
                .orElse(AppMasteryLevel.UNKNOWN);
    }

    /**
     * 取两个掌握等级中较低（更需关注）的一个。
     *
     * @param current   当前等级
     * @param candidate 候选等级
     * @return 较低掌握等级
     */
    private AppMasteryLevel worst(AppMasteryLevel current, AppMasteryLevel candidate) {
        if (current == null) {
            return candidate == null ? AppMasteryLevel.UNKNOWN : candidate;
        }
        if (candidate == null) {
            return current;
        }
        if (current == AppMasteryLevel.UNKNOWN) {
            return candidate;
        }
        if (candidate == AppMasteryLevel.UNKNOWN) {
            return current;
        }
        return current.ordinal() < candidate.ordinal() ? current : candidate;
    }

    /**
     * 按名称查找知识点掌握记录。
     *
     * @param profile 用户学习画像
     * @param name    知识点名称
     * @return 知识点掌握记录，不存在时返回 null
     */
    private AppLearningProfile.KnowledgePointMastery findPoint(AppLearningProfile profile, String name) {
        return profile.getKnowledgePoints().stream()
                .filter(point -> name.equals(point.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 按名称查找或创建知识点掌握记录。
     *
     * @param profile 用户学习画像
     * @param name    知识点名称
     * @return 知识点掌握记录
     */
    private AppLearningProfile.KnowledgePointMastery findOrCreate(AppLearningProfile profile, String name) {
        return profile.getKnowledgePoints().stream()
                .filter(point -> name.equals(point.getName()))
                .findFirst()
                .orElseGet(() -> {
                    AppLearningProfile.KnowledgePointMastery created = new AppLearningProfile.KnowledgePointMastery();
                    created.setName(name);
                    profile.getKnowledgePoints().add(created);
                    return created;
                });
    }
}
