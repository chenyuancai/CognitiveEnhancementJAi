package cn.cyc.ai.cog.platform.operations.domain;

import java.time.LocalDateTime;

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
