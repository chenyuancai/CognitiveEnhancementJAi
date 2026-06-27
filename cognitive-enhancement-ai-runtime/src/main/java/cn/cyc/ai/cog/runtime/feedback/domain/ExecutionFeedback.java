package cn.cyc.ai.cog.runtime.feedback.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 执行反馈记录。
 *
 * @param tenantCode       租户编码
 * @param feedbackId       反馈 ID
 * @param traceId          链路 TraceId
 * @param sessionId        会话 ID
 * @param rating           评分 1-5
 * @param originalAnswer   AI 原始回答
 * @param correctedAnswer  用户修正回答
 * @param comment          反馈备注
 * @param recordedAt       记录时间
 * @author cyc
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
