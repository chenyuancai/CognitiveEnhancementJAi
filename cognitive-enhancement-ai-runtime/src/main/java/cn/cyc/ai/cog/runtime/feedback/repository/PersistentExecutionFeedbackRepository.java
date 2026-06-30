package cn.cyc.ai.cog.runtime.feedback.repository;

import cn.cyc.ai.cog.runtime.feedback.domain.ExecutionFeedback;
import cn.cyc.ai.cog.runtime.feedback.entity.ExecutionFeedbackEntity;
import cn.cyc.ai.cog.runtime.feedback.mapper.ExecutionFeedbackMapper;
import cn.cyc.ai.cog.runtime.feedback.spi.ExecutionFeedbackRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 持久化执行反馈仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentExecutionFeedbackRepository implements ExecutionFeedbackRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PersistentExecutionFeedbackRepository.class);

    /**
     * 执行反馈 Mapper。
     */
    private final ExecutionFeedbackMapper executionFeedbackMapper;

    /**
     * 构造持久化执行反馈仓储。
     *
     * @param executionFeedbackMapper 执行反馈 Mapper
     */
    public PersistentExecutionFeedbackRepository(ExecutionFeedbackMapper executionFeedbackMapper) {
        this.executionFeedbackMapper = executionFeedbackMapper;
    }

    /**
     * 执行save。
     *
     * @param feedback feedback
     */
    @Override
    public void save(ExecutionFeedback feedback) {
        executionFeedbackMapper.insert(toEntity(feedback));
        log.debug("持久化执行反馈, feedbackId={}, traceId={}", feedback.feedbackId(), feedback.traceId());
    }

    /**
     * 查找人链路ID。
     *
     * @param traceId 链路 Trace ID
     * @return 查找结果
     */
    @Override
    public List<ExecutionFeedback> findByTraceId(String traceId) {
        LambdaQueryWrapper<ExecutionFeedbackEntity> queryWrapper = new LambdaQueryWrapper<ExecutionFeedbackEntity>()
                .eq(ExecutionFeedbackEntity::getTenantId, TenantContext.currentTenantId())
                .eq(ExecutionFeedbackEntity::getTraceId, traceId)
                .orderByDesc(ExecutionFeedbackEntity::getRecordedAt)
                .orderByDesc(ExecutionFeedbackEntity::getId);
        return executionFeedbackMapper.selectList(queryWrapper).stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 查找人会话ID。
     *
     * @param sessionId 会话 ID
     * @return 查找结果
     */
    @Override
    public List<ExecutionFeedback> findBySessionId(String sessionId) {
        LambdaQueryWrapper<ExecutionFeedbackEntity> queryWrapper = new LambdaQueryWrapper<ExecutionFeedbackEntity>()
                .eq(ExecutionFeedbackEntity::getTenantId, TenantContext.currentTenantId())
                .eq(ExecutionFeedbackEntity::getSessionId, sessionId)
                .orderByDesc(ExecutionFeedbackEntity::getRecordedAt)
                .orderByDesc(ExecutionFeedbackEntity::getId);
        return executionFeedbackMapper.selectList(queryWrapper).stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 转换为实体。
     *
     * @param feedback feedback
     * @return 转换结果
     */
    private ExecutionFeedbackEntity toEntity(ExecutionFeedback feedback) {
        ExecutionFeedbackEntity entity = new ExecutionFeedbackEntity();
        entity.setTenantId(TenantIds.resolveId(feedback.tenantCode()));
        entity.setFeedbackId(feedback.feedbackId());
        entity.setTraceId(feedback.traceId());
        entity.setSessionId(feedback.sessionId());
        entity.setRating(feedback.rating());
        entity.setOriginalAnswer(feedback.originalAnswer());
        entity.setCorrectedAnswer(feedback.correctedAnswer());
        entity.setCommentText(feedback.comment());
        entity.setRecordedAt(feedback.recordedAt());
        return entity;
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private ExecutionFeedback toDomain(ExecutionFeedbackEntity entity) {
        return new ExecutionFeedback(
                TenantIds.toCode(entity.getTenantId()),
                entity.getFeedbackId(),
                entity.getTraceId(),
                entity.getSessionId(),
                entity.getRating(),
                entity.getOriginalAnswer(),
                entity.getCorrectedAnswer(),
                entity.getCommentText(),
                entity.getRecordedAt()
        );
    }
}
