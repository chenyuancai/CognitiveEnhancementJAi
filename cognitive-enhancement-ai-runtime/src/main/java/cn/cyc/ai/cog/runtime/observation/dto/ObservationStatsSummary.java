package cn.cyc.ai.cog.runtime.observation.dto;

import java.math.BigDecimal;

/**
 * 观测聚合总览摘要。
 *
 * @param totalExecutions      执行总次数
 * @param successExecutions    成功执行次数
 * @param failedExecutions     失败执行次数
 * @param totalUsageRecords    用量记录数
 * @param totalTokens          总 token 数
 * @param totalEstimatedCost   预估总成本
 * @author cyc
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
