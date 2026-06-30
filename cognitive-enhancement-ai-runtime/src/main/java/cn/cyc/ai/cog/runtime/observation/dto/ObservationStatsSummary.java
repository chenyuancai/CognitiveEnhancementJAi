package cn.cyc.ai.cog.runtime.observation.dto;

import java.math.BigDecimal;

/**
 * 观测聚合总览摘要。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ObservationStatsSummary(
        int totalExecutions,
        int successExecutions,
        int failedExecutions,
        int totalUsageRecords,
        long totalTokens,
        BigDecimal totalEstimatedCost
) {
}
