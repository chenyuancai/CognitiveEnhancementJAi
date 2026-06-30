package cn.cyc.ai.cog.runtime.observation.dto;

import java.time.Instant;
import java.util.List;

/**
 * 观测聚合统计结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ObservationStatsResult(
        Instant startTime,
        Instant endTime,
        ObservationStatsSummary summary,
        List<ObservationDimensionStats> byCapability,
        List<ObservationDimensionStats> byModel,
        List<ObservationDimensionStats> byTool
) {
}
