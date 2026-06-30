package cn.cyc.ai.cog.runtime.feedback.dto;

/**
 * 提交执行反馈请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
