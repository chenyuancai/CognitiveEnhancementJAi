package cn.cyc.ai.cog.platform.operations.domain;

import java.time.LocalDateTime;

/**
 * InAppMessage 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record InAppMessage(
        Long id,
        Long tenantId,
        Long userId,
        String templateCode,
        String title,
        String content,
        boolean read,
        LocalDateTime createTime
) {
}
