package cn.cyc.ai.cog.platform.practice.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.practice.entity.PracticeAnswerEntity;
import cn.cyc.ai.cog.platform.practice.entity.PracticeSessionEntity;
import cn.cyc.ai.cog.platform.practice.entity.ReviewPendingEntity;
import cn.cyc.ai.cog.platform.practice.mapper.PracticeAnswerMapper;
import cn.cyc.ai.cog.platform.practice.mapper.PracticeSessionMapper;
import cn.cyc.ai.cog.platform.practice.mapper.ReviewPendingMapper;
import cn.cyc.ai.cog.platform.practice.spi.PracticePersistencePort;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * MyBatis 练习持久化实现。
 */
@Service
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbPracticePersistenceService implements PracticePersistencePort {

    private final PracticeSessionMapper sessionMapper;
    private final PracticeAnswerMapper answerMapper;
    private final ReviewPendingMapper reviewPendingMapper;

    public DbPracticePersistenceService(PracticeSessionMapper sessionMapper,
                                        PracticeAnswerMapper answerMapper,
                                        ReviewPendingMapper reviewPendingMapper) {
        this.sessionMapper = sessionMapper;
        this.answerMapper = answerMapper;
        this.reviewPendingMapper = reviewPendingMapper;
    }

    @Override
    public PracticeSessionEntity saveSession(PracticeSessionEntity session) {
        sessionMapper.insert(session);
        return session;
    }

    @Override
    public void updateSession(PracticeSessionEntity session) {
        sessionMapper.updateById(session);
    }

    @Override
    public Optional<PracticeSessionEntity> findSessionByCode(Long tenantId, Long userId, String sessionCode) {
        return Optional.ofNullable(sessionMapper.selectOne(new LambdaQueryWrapper<PracticeSessionEntity>()
                .eq(PracticeSessionEntity::getTenantId, tenantId)
                .eq(PracticeSessionEntity::getUserId, userId)
                .eq(PracticeSessionEntity::getSessionCode, sessionCode)));
    }

    @Override
    public Optional<PracticeSessionEntity> findSessionById(Long tenantId, Long userId, Long sessionId) {
        PracticeSessionEntity entity = sessionMapper.selectById(sessionId);
        if (entity == null || !tenantId.equals(entity.getTenantId()) || !userId.equals(entity.getUserId())) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    @Override
    public PracticeAnswerEntity saveAnswer(PracticeAnswerEntity answer) {
        answerMapper.insert(answer);
        return answer;
    }

    @Override
    public void updateAnswer(PracticeAnswerEntity answer) {
        answerMapper.updateById(answer);
    }

    @Override
    public Optional<PracticeAnswerEntity> findAnswerById(Long tenantId, Long answerId) {
        PracticeAnswerEntity entity = answerMapper.selectById(answerId);
        if (entity == null || !tenantId.equals(entity.getTenantId())) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    @Override
    public List<PracticeAnswerEntity> listAnswersBySession(Long tenantId, Long sessionId) {
        return answerMapper.selectList(new LambdaQueryWrapper<PracticeAnswerEntity>()
                .eq(PracticeAnswerEntity::getTenantId, tenantId)
                .eq(PracticeAnswerEntity::getSessionId, sessionId)
                .orderByAsc(PracticeAnswerEntity::getCreateTime));
    }

    @Override
    public PageResult<PracticeSessionEntity> pageRecentSessions(Long tenantId, Long userId, long current, long size) {
        Page<PracticeSessionEntity> page = sessionMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<PracticeSessionEntity>()
                        .eq(PracticeSessionEntity::getTenantId, tenantId)
                        .eq(PracticeSessionEntity::getUserId, userId)
                        .eq(PracticeSessionEntity::getStatus, "COMPLETED")
                        .orderByDesc(PracticeSessionEntity::getCreateTime));
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public ReviewPendingEntity saveReviewPending(ReviewPendingEntity pending) {
        reviewPendingMapper.insert(pending);
        return pending;
    }

    @Override
    public PageResult<ReviewPendingEntity> pageReviewPending(Long tenantId, Long userId, long current, long size) {
        Page<ReviewPendingEntity> page = reviewPendingMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<ReviewPendingEntity>()
                        .eq(ReviewPendingEntity::getTenantId, tenantId)
                        .eq(ReviewPendingEntity::getUserId, userId)
                        .eq(ReviewPendingEntity::getStatus, "OPEN")
                        .orderByAsc(ReviewPendingEntity::getDueAt));
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public long countOpenReviewPending(Long tenantId, Long userId) {
        return reviewPendingMapper.selectCount(new LambdaQueryWrapper<ReviewPendingEntity>()
                .eq(ReviewPendingEntity::getTenantId, tenantId)
                .eq(ReviewPendingEntity::getUserId, userId)
                .eq(ReviewPendingEntity::getStatus, "OPEN"));
    }

    @Override
    public long countOverdueReviewPending(Long tenantId, Long userId) {
        return reviewPendingMapper.selectCount(new LambdaQueryWrapper<ReviewPendingEntity>()
                .eq(ReviewPendingEntity::getTenantId, tenantId)
                .eq(ReviewPendingEntity::getUserId, userId)
                .eq(ReviewPendingEntity::getStatus, "OPEN")
                .eq(ReviewPendingEntity::getUrgency, "OVERDUE"));
    }

    @Override
    public List<ReviewPendingEntity> listTopReviewPending(Long tenantId, Long userId, int limit) {
        return reviewPendingMapper.selectList(new LambdaQueryWrapper<ReviewPendingEntity>()
                .eq(ReviewPendingEntity::getTenantId, tenantId)
                .eq(ReviewPendingEntity::getUserId, userId)
                .eq(ReviewPendingEntity::getStatus, "OPEN")
                .orderByAsc(ReviewPendingEntity::getDueAt)
                .last("LIMIT " + limit));
    }

    @Override
    public int countTodayAnswers(Long tenantId, Long userId, LocalDate day) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.atTime(LocalTime.MAX);
        return Math.toIntExact(answerMapper.selectCount(new LambdaQueryWrapper<PracticeAnswerEntity>()
                .eq(PracticeAnswerEntity::getTenantId, tenantId)
                .inSql(PracticeAnswerEntity::getSessionId,
                        "SELECT id FROM qz_app_practice_session WHERE tenant_id = " + tenantId
                                + " AND user_id = " + userId)
                .between(PracticeAnswerEntity::getCreateTime, start, end)));
    }

    @Override
    public int countTodayCorrectAnswers(Long tenantId, Long userId, LocalDate day) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.atTime(LocalTime.MAX);
        return Math.toIntExact(answerMapper.selectCount(new LambdaQueryWrapper<PracticeAnswerEntity>()
                .eq(PracticeAnswerEntity::getTenantId, tenantId)
                .ge(PracticeAnswerEntity::getScore, 60)
                .inSql(PracticeAnswerEntity::getSessionId,
                        "SELECT id FROM qz_app_practice_session WHERE tenant_id = " + tenantId
                                + " AND user_id = " + userId)
                .between(PracticeAnswerEntity::getCreateTime, start, end)));
    }

    @Override
    public Optional<PracticeSessionEntity> findLatestInProgress(Long tenantId, Long userId) {
        return Optional.ofNullable(sessionMapper.selectOne(new LambdaQueryWrapper<PracticeSessionEntity>()
                .eq(PracticeSessionEntity::getTenantId, tenantId)
                .eq(PracticeSessionEntity::getUserId, userId)
                .eq(PracticeSessionEntity::getStatus, "IN_PROGRESS")
                .orderByDesc(PracticeSessionEntity::getUpdateTime)
                .last("LIMIT 1")));
    }
}
