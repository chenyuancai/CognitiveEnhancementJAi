package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

/**
 * C 端 AI 助手对话响应。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringChatResponse {

    /** 会话 ID。 */
    private String sessionId;

    /** 链路 Trace ID。 */
    private String traceId;

    /** 助手消息 ID。 */
    private String messageId;

    /** 识别出的学习意图。 */
    private String intent;

    /** 选用的教学策略。 */
    private String strategy;

    /** 助手回答文本。 */
    private String answer;

    /** 兼容字段，与 {@link #state} 同步。 */
    private boolean needUserReply;

    /** 本轮教学蓝图。 */
    private AppTutoringBlueprint blueprint;

    /** 对话状态。 */
    private AppTutoringChatState state = new AppTutoringChatState();
}
