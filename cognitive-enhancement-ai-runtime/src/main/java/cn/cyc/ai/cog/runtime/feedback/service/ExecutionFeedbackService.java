package cn.cyc.ai.cog.runtime.feedback.service;

import cn.cyc.ai.cog.runtime.feedback.domain.ExecutionFeedback;
import cn.cyc.ai.cog.runtime.feedback.spi.ExecutionFeedbackRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 执行反馈服务。
 *
 * @author cyc
 */
@Service
public class ExecutionFeedbackService {

    /**
     * 执行反馈仓储。
     */
    private final ExecutionFeedbackRepository executionFeedbackRepository;

    /**
     * 构造执行反馈服务。
     *
     * @param executionFeedbackRepository 执行反馈仓储
     */
    public ExecutionFeedbackService(ExecutionFeedbackRepository executionFeedbackRepository) {
        this.executionFeedbackRepository = executionFeedbackRepository;
    }

    /**
     * 提交执行反馈。
     *
     * @param traceId         链路 TraceId
     * @param sessionId       会话 ID
     * @param rating          评分 1-5
     * @param originalAnswer  AI 原始回答
     * @param correctedAnswer 用户修正回答
     * @param comment         反馈备注
     * @return 保存后的反馈记录
     */
    public ExecutionFeedback submitFeedback(String traceId,
                                            String sessionId,
                                            Integer rating,
                                            String originalAnswer,
                                            String correctedAnswer,
                                            String comment) {
        ExecutionFeedback feedback = new ExecutionFeedback(
                TenantContext.currentTenantCode(),
                UUID.randomUUID().toString(),
                traceId,
                sessionId,
                rating,
                originalAnswer,
                correctedAnswer,
                comment,
                Instant.now()
        );
        executionFeedbackRepository.save(feedback);
        return feedback;
    }

    /**
     * 查询执行反馈列表。
     *
     * @param traceId   链路 TraceId 筛选条件
     * @param sessionId 会话 ID 筛选条件
     * @return 反馈列表
     */
    public List<ExecutionFeedback> listFeedback(String traceId, String sessionId) {
        if (StringUtils.hasText(traceId) && StringUtils.hasText(sessionId)) {
            return executionFeedbackRepository.findByTraceId(traceId).stream()
                    .filter(feedback -> sessionId.equals(feedback.sessionId()))
                    .collect(Collectors.toList());
        }
        if (StringUtils.hasText(traceId)) {
            return executionFeedbackRepository.findByTraceId(traceId);
        }
        if (StringUtils.hasText(sessionId)) {
            return executionFeedbackRepository.findBySessionId(sessionId);
        }
        return List.of();
    }
}
