package cn.cyc.ai.cog.platform.operations.domain;

import java.time.LocalDateTime;

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
