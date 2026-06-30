package cn.cyc.ai.cog.app.review.assembler;

import cn.cyc.ai.cog.app.review.dto.AppReviewErrorBookItemVO;
import cn.cyc.ai.cog.app.review.dto.AppReviewPendingItemVO;
import cn.cyc.ai.cog.app.review.dto.AppReviewRecentSessionVO;
import cn.cyc.ai.cog.app.tutoring.dto.AppMistakeRecordVO;
import cn.cyc.ai.cog.platform.practice.entity.PracticeSessionEntity;
import cn.cyc.ai.cog.platform.practice.entity.ReviewPendingEntity;
import cn.cyc.ai.cog.platform.tutoring.entity.MistakeRecordEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 复习域 Entity → 契约 VO 转换器。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Component
public class AppReviewVoAssembler {

    public AppReviewPendingItemVO toPending(ReviewPendingEntity entity) {
        AppReviewPendingItemVO vo = new AppReviewPendingItemVO();
        vo.setId(String.valueOf(entity.getId()));
        vo.setTitle(entity.getTitle());
        vo.setUrgency(entity.getUrgency());
        vo.setDueText(buildDueText(entity.getDueAt()));
        vo.setTag(entity.getTag());
        vo.setAccuracy(entity.getAccuracy());
        vo.setDuration("约5分钟");
        return vo;
    }

    public AppReviewErrorBookItemVO toErrorBook(MistakeRecordEntity entity) {
        AppReviewErrorBookItemVO vo = new AppReviewErrorBookItemVO();
        vo.setId(String.valueOf(entity.getId()));
        vo.setTitle(entity.getKnowledgePoint());
        vo.setContentId(entity.getContentId());
        vo.setScore(entity.getScore());
        vo.setTag(entity.getTag());
        return vo;
    }

    public AppReviewErrorBookItemVO toErrorBook(AppMistakeRecordVO vo) {
        AppReviewErrorBookItemVO item = new AppReviewErrorBookItemVO();
        item.setId(vo.getId() == null ? null : String.valueOf(vo.getId()));
        item.setTitle(vo.getKnowledgePoint());
        item.setContentId(vo.getContentId());
        item.setScore(vo.getScore());
        item.setTag(vo.getTag());
        return item;
    }

    public AppReviewRecentSessionVO toRecent(PracticeSessionEntity entity) {
        AppReviewRecentSessionVO vo = new AppReviewRecentSessionVO();
        vo.setId(entity.getSessionCode());
        vo.setTitle(entity.getTitle());
        vo.setAccuracy(entity.getAccuracy());
        vo.setQuestions(entity.getQuestionCount());
        vo.setMinutes(entity.getMinutes());
        vo.setMode(entity.getMode());
        return vo;
    }

    private String buildDueText(LocalDateTime dueAt) {
        if (dueAt == null) {
            return "待安排";
        }
        long days = ChronoUnit.DAYS.between(dueAt.toLocalDate(), LocalDateTime.now().toLocalDate());
        if (days > 0) {
            return "超期" + days + "天";
        }
        return "今日到期";
    }
}
