package cn.cyc.ai.cog.runtime.harness.dto;

import java.time.Instant;

/**
 * Harness 报告摘要（用于列表展示）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record HarnessReportSummary(
        String harnessId,
        String status,
        Instant startTime,
        long totalDurationMs,
        String scenarioDesc
) {
}
