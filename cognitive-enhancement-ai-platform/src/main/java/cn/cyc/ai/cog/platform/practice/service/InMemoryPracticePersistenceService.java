package cn.cyc.ai.cog.platform.practice.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.practice.entity.PracticeAnswerEntity;
import cn.cyc.ai.cog.platform.practice.entity.PracticeSessionEntity;
import cn.cyc.ai.cog.platform.practice.entity.ReviewPendingEntity;
import cn.cyc.ai.cog.platform.practice.spi.PracticePersistencePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存练习持久化（IT / 无 DB 模式）。
 */
@Service
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = false)
public class InMemoryPracticePersistenceService implements PracticePersistencePort {

    private final AtomicLong sessionSeq = new AtomicLong(1);
    private final AtomicLong answerSeq = new AtomicLong(1);
    private final AtomicLong pendingSeq = new AtomicLong(1);

    private final Map<Long, PracticeSessionEntity> sessions = new ConcurrentHashMap<>();
    private final Map<Long, PracticeAnswerEntity> answers = new ConcurrentHashMap<>();
    private final Map<Long, ReviewPendingEntity> pendingItems = new ConcurrentHashMap<>();

    @Override
    public PracticeSessionEntity saveSession(PracticeSessionEntity session) {
        if (session.getId() == null) {
            session.setId(sessionSeq.getAndIncrement());
        }
        LocalDateTime now = LocalDateTime.now();
        session.setCreateTime(now);
        session.setUpdateTime(now);
        sessions.put(session.getId(), session);
        return session;
    }

    @Override
    public void updateSession(PracticeSessionEntity session) {
        session.setUpdateTime(LocalDateTime.now());
        sessions.put(session.getId(), session);
    }

    @Override
    public Optional<PracticeSessionEntity> findSessionByCode(Long tenantId, Long userId, String sessionCode) {
        return sessions.values().stream()
                .filter(s -> tenantId.equals(s.getTenantId())
                        && userId.equals(s.getUserId())
                        && sessionCode.equals(s.getSessionCode()))
                .findFirst();
    }

    @Override
    public Optional<PracticeSessionEntity> findSessionById(Long tenantId, Long userId, Long sessionId) {
        PracticeSessionEntity entity = sessions.get(sessionId);
        if (entity == null || !tenantId.equals(entity.getTenantId()) || !userId.equals(entity.getUserId())) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    @Override
    public PracticeAnswerEntity saveAnswer(PracticeAnswerEntity answer) {
        if (answer.getId() == null) {
            answer.setId(answerSeq.getAndIncrement());
        }
        answer.setCreateTime(LocalDateTime.now());
        answers.put(answer.getId(), answer);
        return answer;
    }

    @Override
    public void updateAnswer(PracticeAnswerEntity answer) {
        answers.put(answer.getId(), answer);
    }

    @Override
    public Optional<PracticeAnswerEntity> findAnswerById(Long tenantId, Long answerId) {
        PracticeAnswerEntity entity = answers.get(answerId);
        if (entity == null || !tenantId.equals(entity.getTenantId())) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    @Override
    public List<PracticeAnswerEntity> listAnswersBySession(Long tenantId, Long sessionId) {
        return answers.values().stream()
                .filter(a -> tenantId.equals(a.getTenantId()) && sessionId.equals(a.getSessionId()))
                .sorted(Comparator.comparing(PracticeAnswerEntity::getCreateTime))
                .toList();
    }

    @Override
    public PageResult<PracticeSessionEntity> pageRecentSessions(Long tenantId, Long userId, long current, long size) {
        List<PracticeSessionEntity> all = sessions.values().stream()
                .filter(s -> tenantId.equals(s.getTenantId())
                        && userId.equals(s.getUserId())
                        && "COMPLETED".equals(s.getStatus()))
                .sorted(Comparator.comparing(PracticeSessionEntity::getCreateTime).reversed())
                .toList();
        return slice(all, current, size);
    }

    @Override
    public ReviewPendingEntity saveReviewPending(ReviewPendingEntity pending) {
        if (pending.getId() == null) {
            pending.setId(pendingSeq.getAndIncrement());
        }
        LocalDateTime now = LocalDateTime.now();
        pending.setCreateTime(now);
        pending.setUpdateTime(now);
        pendingItems.put(pending.getId(), pending);
        return pending;
    }

    @Override
    public PageResult<ReviewPendingEntity> pageReviewPending(Long tenantId, Long userId, long current, long size) {
        List<ReviewPendingEntity> all = pendingItems.values().stream()
                .filter(p -> tenantId.equals(p.getTenantId())
                        && userId.equals(p.getUserId())
                        && "OPEN".equals(p.getStatus()))
                .sorted(Comparator.comparing(ReviewPendingEntity::getDueAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        return slice(all, current, size);
    }

    @Override
    public long countOpenReviewPending(Long tenantId, Long userId) {
        return pendingItems.values().stream()
                .filter(p -> tenantId.equals(p.getTenantId())
                        && userId.equals(p.getUserId())
                        && "OPEN".equals(p.getStatus()))
                .count();
    }

    @Override
    public long countOverdueReviewPending(Long tenantId, Long userId) {
        return pendingItems.values().stream()
                .filter(p -> tenantId.equals(p.getTenantId())
                        && userId.equals(p.getUserId())
                        && "OPEN".equals(p.getStatus())
                        && "OVERDUE".equals(p.getUrgency()))
                .count();
    }

    @Override
    public List<ReviewPendingEntity> listTopReviewPending(Long tenantId, Long userId, int limit) {
        return pendingItems.values().stream()
                .filter(p -> tenantId.equals(p.getTenantId())
                        && userId.equals(p.getUserId())
                        && "OPEN".equals(p.getStatus()))
                .sorted(Comparator.comparing(ReviewPendingEntity::getDueAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(limit)
                .toList();
    }

    @Override
    public int countTodayAnswers(Long tenantId, Long userId, LocalDate day) {
        return (int) answers.values().stream()
                .filter(a -> tenantId.equals(a.getTenantId()) && isSameDay(a.getCreateTime(), day))
                .filter(a -> belongsToUser(a.getSessionId(), tenantId, userId))
                .count();
    }

    @Override
    public int countTodayCorrectAnswers(Long tenantId, Long userId, LocalDate day) {
        return (int) answers.values().stream()
                .filter(a -> tenantId.equals(a.getTenantId()) && isSameDay(a.getCreateTime(), day))
                .filter(a -> belongsToUser(a.getSessionId(), tenantId, userId))
                .filter(a -> a.getScore() != null && a.getScore() >= 60)
                .count();
    }

    @Override
    public Optional<PracticeSessionEntity> findLatestInProgress(Long tenantId, Long userId) {
        return sessions.values().stream()
                .filter(s -> tenantId.equals(s.getTenantId())
                        && userId.equals(s.getUserId())
                        && "IN_PROGRESS".equals(s.getStatus()))
                .max(Comparator.comparing(PracticeSessionEntity::getUpdateTime));
    }

    private boolean belongsToUser(Long sessionId, Long tenantId, Long userId) {
        PracticeSessionEntity session = sessions.get(sessionId);
        return session != null && tenantId.equals(session.getTenantId()) && userId.equals(session.getUserId());
    }

    private boolean isSameDay(LocalDateTime time, LocalDate day) {
        return time != null && time.toLocalDate().equals(day);
    }

    private <T> PageResult<T> slice(List<T> all, long current, long size) {
        long page = current < 1 ? 1 : current;
        long pageSize = size < 1 ? 10 : size;
        int from = (int) ((page - 1) * pageSize);
        if (from >= all.size()) {
            return PageResult.empty(page, pageSize);
        }
        int to = (int) Math.min(from + pageSize, all.size());
        return PageResult.of(new ArrayList<>(all.subList(from, to)), all.size(), page, pageSize);
    }
}
