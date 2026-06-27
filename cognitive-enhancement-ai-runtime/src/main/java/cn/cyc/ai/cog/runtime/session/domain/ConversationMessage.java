package cn.cyc.ai.cog.runtime.session.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 会话消息记录。
 *
 * @param tenantCode  租户编码
 * @param messageId   消息 ID
 * @param sessionId   会话 ID
 * @param role        消息角色
 * @param content     消息内容
 * @param traceId     关联 TraceId
 * @param recordedAt  记录时间
 * @author cyc
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
