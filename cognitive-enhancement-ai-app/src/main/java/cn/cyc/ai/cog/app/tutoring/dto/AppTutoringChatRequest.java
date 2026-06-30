package cn.cyc.ai.cog.app.tutoring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * C 端 AI 助手对话请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringChatRequest {

    /** 会话 ID；为空时自动创建会话。 */
    private String sessionId;

    /** 用户消息。 */
    /** 消息。 */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /** 会话能力码，默认 capability.chat.tutoring。 */
    private String capabilityCode;

    /** 引用上下文。 */
    private AppTutoringReferences references = new AppTutoringReferences();

    /** 调用选项。 */
    private AppTutoringChatOptions options = new AppTutoringChatOptions();
}
