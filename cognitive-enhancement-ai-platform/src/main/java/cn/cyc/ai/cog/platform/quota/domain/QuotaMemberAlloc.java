package cn.cyc.ai.cog.platform.quota.domain;

public record QuotaMemberAlloc(
        Long id,
        Long accountId,
        Long userId,
        Long allocatedAmount,
        Long usedAmount
) {
}
