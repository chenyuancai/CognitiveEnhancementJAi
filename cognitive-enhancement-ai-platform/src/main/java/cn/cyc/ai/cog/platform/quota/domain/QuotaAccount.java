package cn.cyc.ai.cog.platform.quota.domain;

import java.time.LocalDateTime;

/**
 * QuotaAccount 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
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
