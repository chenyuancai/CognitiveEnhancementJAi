package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

import java.time.Instant;

/**
 * C 端学习辅导会话消息视图。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringMessageVO {

    /** 消息 ID。 */
    private String messageId;

    /** 消息角色。 */
    private String role;

    /** 消息内容。 */
    private String content;

    /** 关联 Trace ID。 */
    private String traceId;

    /** 记录时间。 */
    private Instant recordedAt;
}
