package cn.cyc.ai.cog.platform.operations.domain;

import java.time.LocalDateTime;

public record Banner(
        Long id,
        String title,
        String imageUrl,
        String linkUrl,
        String position,
        Integer sortNo,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
