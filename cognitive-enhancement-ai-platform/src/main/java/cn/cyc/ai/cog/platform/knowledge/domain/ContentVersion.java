package cn.cyc.ai.cog.platform.knowledge.domain;

import java.time.LocalDateTime;

/**
 * 内容发布版本快照。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ContentVersion(
        Long id,
        Long contentId,
        int versionNo,
        String title,
        String summary,
        String body,
        String minLevelCode,
        Long operatorId,
        LocalDateTime createTime
) {
}
