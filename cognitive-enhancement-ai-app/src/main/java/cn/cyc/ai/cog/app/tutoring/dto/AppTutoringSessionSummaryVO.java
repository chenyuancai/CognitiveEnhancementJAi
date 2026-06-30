package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

import java.time.Instant;

/**
 * C 端学习辅导会话摘要。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringSessionSummaryVO {

    /** 会话 ID。 */
    private String sessionId;

    /** 会话标题。 */
    private String title;

    /** 会话能力码。 */
    private String capabilityCode;

    /** 会话状态。 */
    private String status;

    /** 创建时间。 */
    private Instant createdAt;

    /** 最后更新时间。 */
    private Instant updatedAt;

    /** 最后一条消息预览。 */
    private String lastMessagePreview;
}
