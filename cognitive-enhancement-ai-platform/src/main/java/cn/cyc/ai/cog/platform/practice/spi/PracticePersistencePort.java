package cn.cyc.ai.cog.platform.practice.spi;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.practice.entity.PracticeAnswerEntity;
import cn.cyc.ai.cog.platform.practice.entity.PracticeSessionEntity;
import cn.cyc.ai.cog.platform.practice.entity.ReviewPendingEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 练习与复习持久化端口（C 端 app 经此访问，不直连 Mapper）。
 */
public interface PracticePersistencePort {

    PracticeSessionEntity saveSession(PracticeSessionEntity session);

    void updateSession(PracticeSessionEntity session);

    Optional<PracticeSessionEntity> findSessionByCode(Long tenantId, Long userId, String sessionCode);

    Optional<PracticeSessionEntity> findSessionById(Long tenantId, Long userId, Long sessionId);

    PracticeAnswerEntity saveAnswer(PracticeAnswerEntity answer);

    void updateAnswer(PracticeAnswerEntity answer);

    Optional<PracticeAnswerEntity> findAnswerById(Long tenantId, Long answerId);

    List<PracticeAnswerEntity> listAnswersBySession(Long tenantId, Long sessionId);

    PageResult<PracticeSessionEntity> pageRecentSessions(Long tenantId, Long userId, long current, long size);

    ReviewPendingEntity saveReviewPending(ReviewPendingEntity pending);

    PageResult<ReviewPendingEntity> pageReviewPending(Long tenantId, Long userId, long current, long size);

    long countOpenReviewPending(Long tenantId, Long userId);

    long countOverdueReviewPending(Long tenantId, Long userId);

    List<ReviewPendingEntity> listTopReviewPending(Long tenantId, Long userId, int limit);

    int countTodayAnswers(Long tenantId, Long userId, LocalDate day);

    int countTodayCorrectAnswers(Long tenantId, Long userId, LocalDate day);

    Optional<PracticeSessionEntity> findLatestInProgress(Long tenantId, Long userId);
}
