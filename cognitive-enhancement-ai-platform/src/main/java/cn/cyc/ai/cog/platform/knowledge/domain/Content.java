package cn.cyc.ai.cog.platform.knowledge.domain;

import java.time.LocalDateTime;

/**
 * Content 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record Content(
        Long id,
        String title,
        String contentType,
        String author,
        String status,
        String summary,
        String body,
        String auditRemark,
        String minLevelCode,
        Integer currentVersion,
        LocalDateTime publishedAt
) {
}
