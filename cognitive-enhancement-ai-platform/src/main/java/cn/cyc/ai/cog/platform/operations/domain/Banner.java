package cn.cyc.ai.cog.platform.operations.domain;

import java.time.LocalDateTime;

/**
 * Banner 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
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
