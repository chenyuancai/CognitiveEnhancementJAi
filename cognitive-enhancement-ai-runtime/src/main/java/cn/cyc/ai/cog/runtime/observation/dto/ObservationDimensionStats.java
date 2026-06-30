package cn.cyc.ai.cog.runtime.observation.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 单维度观测聚合统计。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ObservationDimensionStats(
        String dimensionKey,
        int invocationCount,
        int successCount,
        int failedCount,
        long totalTokens,
        BigDecimal totalEstimatedCost,
        Instant lastRecordedAt
) {
}
