package cn.cyc.ai.cog.platform.membership.domain;

import java.time.LocalDateTime;

/**
 * AccountMembership 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AccountMembership(
        Long id,
        Long tenantId,
        Long accountId,
        Long levelId,
        String levelCode,
        LocalDateTime expireAt,
        String source
) {
}
