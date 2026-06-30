package cn.cyc.ai.cog.app.insights.service;

import cn.cyc.ai.cog.app.insights.assembler.AppInsightsAssembler;
import cn.cyc.ai.cog.app.insights.dto.AppInsightsOverviewVO;
import cn.cyc.ai.cog.app.practice.service.AppPracticeInsightService;
import cn.cyc.ai.cog.app.review.dto.AppReviewRecentSessionVO;
import cn.cyc.ai.cog.app.review.service.AppReviewService;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringProfileService;
import cn.cyc.ai.cog.app.tutoring.support.InMemoryMistakeStore;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学习画像概览 BFF（聚合练习洞察、辅导画像、复习记录）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Service
public class AppInsightsService {

    private final AppPracticeInsightService practiceInsightService;
    private final AppReviewService reviewService;
    private final AppTutoringProfileService profileService;
    private final AppInsightsAssembler assembler;
    private final ObjectProvider<InMemoryMistakeStore> inMemoryMistakeStoreProvider;

    public AppInsightsService(AppPracticeInsightService practiceInsightService,
                              AppReviewService reviewService,
                              AppTutoringProfileService profileService,
                              AppInsightsAssembler assembler,
                              ObjectProvider<InMemoryMistakeStore> inMemoryMistakeStoreProvider) {
        this.practiceInsightService = practiceInsightService;
        this.reviewService = reviewService;
        this.profileService = profileService;
        this.assembler = assembler;
        this.inMemoryMistakeStoreProvider = inMemoryMistakeStoreProvider;
    }

    /**
     * 构建学习画像概览 BFF。
     */
    public AppInsightsOverviewVO buildOverview() {
        if (UserContext.currentUserId() == null) {
            throw Errors.of(PlatformErrorCode.UNAUTHORIZED);
        }
        var practiceInsight = practiceInsightService.insightForCurrentUser();
        List<AppReviewRecentSessionVO> recent = reviewService.listRecentForInsights(10);
        AppLearningProfile profile = profileService.loadForCurrentUser();

        AppInsightsOverviewVO vo = new AppInsightsOverviewVO();
        List<Map<String, Object>> stats = new ArrayList<>(practiceInsight.getTodayStats());
        stats.add(Map.of("value", String.valueOf(mistakeCount()), "label", "错题数"));
        vo.setOverviewStats(stats);
        vo.setTypeDistribution(Map.of("total", recent.size(), "segments", List.of()));
        vo.setTagMastery(buildTagMastery(profile));
        vo.setWeakPoints(buildWeakPoints(profile));
        vo.setHeatmap(Map.of("weeks", List.of()));
        vo.setAccuracyTrendPoints(assembler.buildAccuracyTrend(recent));
        vo.setAccuracyTrendHighlights(assembler.buildHighlights(recent));
        vo.setRecentSessions(recent.stream().map(this::toRecentMap).toList());
        return vo;
    }

    private long mistakeCount() {
        InMemoryMistakeStore store = inMemoryMistakeStoreProvider.getIfAvailable();
        if (store == null) {
            return 0L;
        }
        return store.page(UserContext.currentUserId(), 1, 1).getTotal();
    }

    private List<Map<String, Object>> buildTagMastery(AppLearningProfile profile) {
        if (profile == null || profile.getKnowledgePoints() == null) {
            return List.of();
        }
        return profile.getKnowledgePoints().stream()
                .map(point -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("tag", point.getName());
                    item.put("mastery", point.getMastery() == null ? "UNKNOWN" : point.getMastery().name());
                    return item;
                }).toList();
    }

    private List<Map<String, Object>> buildWeakPoints(AppLearningProfile profile) {
        if (profile == null || profile.getWeakTopics() == null) {
            return List.of();
        }
        return profile.getWeakTopics().stream()
                .map(topic -> Map.<String, Object>of("title", topic))
                .toList();
    }

    private Map<String, Object> toRecentMap(AppReviewRecentSessionVO session) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", session.getId());
        map.put("title", session.getTitle());
        map.put("accuracy", session.getAccuracy());
        map.put("questions", session.getQuestions());
        map.put("minutes", session.getMinutes());
        map.put("mode", session.getMode());
        return map;
    }
}
