package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

/**
 * 异步学习辅导受理响应。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringChatAcceptedResponse {

    /** 链路 Trace ID。 */
    private String traceId;

    /** 会话 ID。 */
    private String sessionId;

    /** 是否已受理。 */
    private boolean accepted = true;
}
