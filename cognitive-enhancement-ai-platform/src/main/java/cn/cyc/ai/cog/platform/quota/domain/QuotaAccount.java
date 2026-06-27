package cn.cyc.ai.cog.platform.quota.domain;

import java.time.LocalDateTime;

public record QuotaAccount(
        Long id,
        Long tenantId,
        Long accountId,
        Long cycleRemaining,
        Long cycleTotal,
        LocalDateTime cycleResetAt,
        Long giftRemaining,
        Long giftTotal,
        Long topupRemaining,
        Long topupTotal
) {
}
