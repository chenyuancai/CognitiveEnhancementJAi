package cn.cyc.ai.cog.runtime.feedback.repository;

import cn.cyc.ai.cog.runtime.feedback.domain.ExecutionFeedback;
import cn.cyc.ai.cog.runtime.feedback.spi.ExecutionFeedbackRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 内存执行反馈仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryExecutionFeedbackRepository implements ExecutionFeedbackRepository {

    /**
     * 内存反馈容器。
     */
    private final CopyOnWriteArrayList<ExecutionFeedback> feedbacks = new CopyOnWriteArrayList<>();

    @Override
    public void save(ExecutionFeedback feedback) {
        feedbacks.add(feedback);
    }

    @Override
    public List<ExecutionFeedback> findByTraceId(String traceId) {
        String tenantCode = TenantContext.currentTenantCode();
        return feedbacks.stream()
                .filter(feedback -> tenantCode.equals(feedback.tenantCode()))
                .filter(feedback -> traceId.equals(feedback.traceId()))
                .sorted(Comparator.comparing(ExecutionFeedback::recordedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<ExecutionFeedback> findBySessionId(String sessionId) {
        String tenantCode = TenantContext.currentTenantCode();
        return feedbacks.stream()
                .filter(feedback -> tenantCode.equals(feedback.tenantCode()))
                .filter(feedback -> sessionId.equals(feedback.sessionId()))
                .sorted(Comparator.comparing(ExecutionFeedback::recordedAt).reversed())
                .collect(Collectors.toList());
    }
}
