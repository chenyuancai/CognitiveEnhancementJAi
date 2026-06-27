package cn.cyc.ai.cog.runtime.session.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 会话记录。
 *
 * @param tenantCode      租户编码
 * @param sessionId       会话 ID
 * @param userId          用户 ID
 * @param capabilityCode  能力编码
 * @param title           会话标题
 * @param status          会话状态
 * @param createdAt       创建时间
 * @param updatedAt       更新时间
 * @author cyc
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
