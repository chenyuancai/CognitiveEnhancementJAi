package cn.cyc.ai.cog.app.tutoring.cache;

import java.time.Instant;

/**
 * Redis 热历史中的轻量消息记录。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AppTutoringCachedMessage(
        String messageId,
        String role,
        String content,
        String traceId,
        Instant recordedAt
) {
}
