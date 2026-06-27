package cn.cyc.ai.cog.platform.membership.domain;

import java.time.LocalDateTime;

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
