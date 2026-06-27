package cn.cyc.ai.cog.platform.operations.domain;

import java.time.LocalDateTime;

public record Announcement(
        Long id,
        String title,
        String body,
        String status,
        LocalDateTime publishAt,
        String targetLevelCodes,
        String targetUserIds
) {
}
