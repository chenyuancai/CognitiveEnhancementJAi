package cn.cyc.ai.cog.runtime.feedback.dto;

/**
 * 提交执行反馈请求。
 *
 * @param traceId          链路 TraceId
 * @param sessionId        会话 ID
 * @param rating           评分 1-5
 * @param originalAnswer   AI 原始回答
 * @param correctedAnswer  用户修正回答
 * @param comment          反馈备注
 * @author cyc
 */
public record SubmitFeedbackRequest(
        String traceId,
        String sessionId,
        Integer rating,
        String originalAnswer,
        String correctedAnswer,
        String comment
) {
}
