package cn.cyc.ai.cog.runtime.observation.dto;

import java.time.Instant;
import java.util.List;

/**
 * 观测聚合统计结果。
 *
 * @param startTime     统计起始时间（请求参数回显，可为 null）
 * @param endTime       统计结束时间（请求参数回显，可为 null）
 * @param summary       总览摘要
 * @param byCapability  按能力聚合
 * @param byModel       按模型聚合
 * @param byTool        按 Tool 聚合
 * @author cyc
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
