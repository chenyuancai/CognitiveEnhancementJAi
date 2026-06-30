package cn.cyc.ai.cog.platform.operations.domain;

import java.time.LocalDateTime;

/**
 * Announcement 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
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
