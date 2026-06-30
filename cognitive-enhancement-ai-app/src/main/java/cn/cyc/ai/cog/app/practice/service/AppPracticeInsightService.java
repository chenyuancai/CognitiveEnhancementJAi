package cn.cyc.ai.cog.app.practice.service;

import cn.cyc.ai.cog.app.practice.dto.AppPracticeInsightVO;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.platform.practice.entity.PracticeSessionEntity;
import cn.cyc.ai.cog.platform.practice.spi.PracticePersistencePort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 练习洞察查询（今日统计、待复习紧迫度、进行中会话）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Service
public class AppPracticeInsightService {

    private final PracticePersistencePort practicePersistence;

    public AppPracticeInsightService(PracticePersistencePort practicePersistence) {
        this.practicePersistence = practicePersistence;
    }

    /**
     * 查询当前用户练习洞察。
     */
    public AppPracticeInsightVO insightForCurrentUser() {
        Long userId = UserContext.currentUserId();
        Long tenantId = TenantContext.currentTenantId();
        AppPracticeInsightVO vo = new AppPracticeInsightVO();
        if (userId == null) {
            return vo;
        }
        LocalDate today = LocalDate.now();
        int completed = practicePersistence.countTodayAnswers(tenantId, userId, today);
        int correct = practicePersistence.countTodayCorrectAnswers(tenantId, userId, today);
        String accuracy = completed == 0 ? "0%" : Math.round(correct * 100.0 / completed) + "%";
        vo.setTodayStats(List.of(
                Map.of("value", String.valueOf(completed), "label", "已完成题数", "variant", "accent"),
                Map.of("value", accuracy, "label", "正确率")));
        vo.setReviewUrgency(Map.of(
                "count", practicePersistence.countOpenReviewPending(tenantId, userId),
                "title", "待复习知识条目",
                "desc", "建议尽快开始新一轮练习"));
        Optional<PracticeSessionEntity> inProgress = practicePersistence.findLatestInProgress(tenantId, userId);
        inProgress.ifPresent(session -> vo.setSession(Map.of(
                "status", "进行中",
                "progress", session.getAnsweredCount() + "/" + session.getQuestionCount() + " 题",
                "percent", session.getQuestionCount() == 0 ? 0
                        : Math.round(session.getAnsweredCount() * 100.0 / session.getQuestionCount()))));
        return vo;
    }
}
