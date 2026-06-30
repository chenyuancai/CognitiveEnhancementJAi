package cn.cyc.ai.cog.platform.operations.domain;

import java.time.LocalDateTime;

/**
 * SupportTicket 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record SupportTicket(
        Long id,
        String ticketNo,
        String title,
        String body,
        String category,
        String status,
        String priority,
        Long submitterUserId,
        Long assigneeUserId,
        LocalDateTime resolvedAt
) {
}
