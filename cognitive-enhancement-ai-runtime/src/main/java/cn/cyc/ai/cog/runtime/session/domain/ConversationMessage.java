package cn.cyc.ai.cog.runtime.session.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 会话消息记录。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ConversationMessage(
        String tenantCode,
        String messageId,
        String sessionId,
        MessageRole role,
        String content,
        String traceId,
        Instant recordedAt
) {

    public ConversationMessage {
        tenantCode = TenantContext.normalize(tenantCode);
    }
}
