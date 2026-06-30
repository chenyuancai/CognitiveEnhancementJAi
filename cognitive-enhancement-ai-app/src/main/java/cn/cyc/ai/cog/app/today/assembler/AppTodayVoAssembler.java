package cn.cyc.ai.cog.app.today.assembler;

import cn.cyc.ai.cog.app.dto.AppAnnouncementVO;
import cn.cyc.ai.cog.app.dto.AppBannerVO;
import cn.cyc.ai.cog.app.dto.AppMeResponse;
import cn.cyc.ai.cog.app.review.dto.AppReviewPendingSummaryVO;
import cn.cyc.ai.cog.app.today.dto.AppTodayVO;
import cn.cyc.ai.cog.platform.practice.entity.PracticeSessionEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 今日页 BFF 各区块 → {@link AppTodayVO} 组装器。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Component
public class AppTodayVoAssembler {

    /**
     * 构建欢迎区数据。
     *
     * @param me                 当前用户摘要
     * @param completedQuestions 今日已完成题数
     * @return welcome 区块
     */
    public Map<String, Object> welcome(AppMeResponse me, int completedQuestions) {
        Map<String, Object> welcome = new HashMap<>();
        welcome.put("userName", me.getUser() == null ? "学习者" : me.getUser().getDisplayName());
        welcome.put("studiedMinutes", Math.max(1, completedQuestions));
        welcome.put("completedQuestions", completedQuestions);
        welcome.put("streakDays", 0);
        return welcome;
    }

    /**
     * 构建今日统计卡片。
     *
     * @param completedQuestions 今日已完成题数
     * @return statCards 列表
     */
    public List<Map<String, Object>> statCards(int completedQuestions) {
        return List.of(Map.of(
                "icon", "check",
                "tone", "forest",
                "value", String.valueOf(completedQuestions),
                "label", "今日做题"));
    }

    /**
     * 构建「继续学习」卡片。
     *
     * @param session 进行中的练习会话
     * @return resumeLearning 区块
     */
    public Map<String, Object> resumeLearning(PracticeSessionEntity session) {
        Map<String, Object> resume = new HashMap<>();
        resume.put("title", "继续学习：" + session.getTitle());
        resume.put("progress", session.getQuestionCount() == 0 ? 0
                : Math.round(session.getAnsweredCount() * 100.0 / session.getQuestionCount()));
        resume.put("lastPosition", "上次练习进行中");
        resume.put("lastActive", "刚刚");
        resume.put("path", "/practice/" + session.getSessionCode());
        return resume;
    }

    /**
     * 构建复习区摘要。
     *
     * @param pending 待复习汇总
     * @return reviewSection 区块
     */
    public Map<String, Object> reviewSection(AppReviewPendingSummaryVO pending) {
        Map<String, Object> reviewSection = new HashMap<>();
        reviewSection.put("total", pending.getTotal());
        reviewSection.put("overdueCount", pending.getOverdueCount());
        reviewSection.put("items", pending.getItems());
        return reviewSection;
    }

    /**
     * 聚合今日页完整响应。
     */
    public AppTodayVO assemble(Map<String, Object> welcome,
                               List<Map<String, Object>> statCards,
                               Map<String, Object> resumeLearning,
                               Map<String, Object> reviewSection,
                               List<Map<String, Object>> recommendations,
                               Map<String, Object> importStatus,
                               List<AppBannerVO> banners,
                               List<AppAnnouncementVO> announcements) {
        AppTodayVO vo = new AppTodayVO();
        vo.setWelcome(welcome);
        vo.setStatCards(statCards);
        vo.setResumeLearning(resumeLearning);
        vo.setReviewSection(reviewSection);
        vo.setRecommendations(recommendations);
        vo.setImportStatus(importStatus);
        vo.setBanners(banners);
        vo.setAnnouncements(announcements);
        return vo;
    }
}
