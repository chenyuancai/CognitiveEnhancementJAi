package cn.cyc.ai.cog.platform.quota.domain;

/**
 * QuotaMemberAlloc 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record QuotaMemberAlloc(
        Long id,
        Long accountId,
        Long userId,
        Long allocatedAmount,
        Long usedAmount
) {
}
