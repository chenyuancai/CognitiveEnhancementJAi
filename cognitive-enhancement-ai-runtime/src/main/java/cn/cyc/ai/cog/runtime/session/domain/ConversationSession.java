package cn.cyc.ai.cog.runtime.session.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 会话记录。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ConversationSession(
        String tenantCode,
        String sessionId,
        String userId,
        String capabilityCode,
        String title,
        SessionStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public ConversationSession {
        tenantCode = TenantContext.normalize(tenantCode);
    }
}
