package cn.cyc.ai.cog.runtime.harness.dto;

import java.time.Instant;

/**
 * Harness 报告摘要（用于列表展示）。
 *
 * @param harnessId       Harness 执行标识
 * @param status          整体状态
 * @param startTime       开始时间
 * @param totalDurationMs 总耗时
 * @param scenarioDesc    场景描述
 * @author cyc
 */
public record HarnessReportSummary(
        String harnessId,
        String status,
        Instant startTime,
        long totalDurationMs,
        String scenarioDesc
) {
}
