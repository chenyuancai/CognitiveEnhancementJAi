package cn.cyc.ai.cog.runtime.feedback.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 执行反馈记录。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ExecutionFeedback(
        String tenantCode,
        String feedbackId,
        String traceId,
        String sessionId,
        Integer rating,
        String originalAnswer,
        String correctedAnswer,
        String comment,
        Instant recordedAt
) {

    public ExecutionFeedback {
        tenantCode = TenantContext.normalize(tenantCode);
    }
}
