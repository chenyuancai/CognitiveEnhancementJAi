package cn.cyc.ai.cog.runtime.observation.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 单维度观测聚合统计。
 *
 * @param dimensionKey       维度键（capabilityCode / modelCode / toolCode）
 * @param invocationCount    调用次数
 * @param successCount       成功次数（仅能力维度有值）
 * @param failedCount        失败次数（仅能力维度有值）
 * @param totalTokens        总 token 数
 * @param totalEstimatedCost 预估总成本
 * @param lastRecordedAt     最近记录时间
 * @author cyc
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
