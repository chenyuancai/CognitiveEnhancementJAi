package cn.cyc.ai.cog.admin.ai.dto;

import cn.cyc.ai.cog.admin.operation.dto.OperationDashboardQuery;
import cn.cyc.ai.cog.admin.operation.dto.OperationDashboardResult;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 成本看板（Admin 只读聚合）。
 */
@Data
public class AiCostDashboardResult {

    private String preset;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private long aiInvocationCount;
    private long tokenDeltaInRange;
    private List<OperationDashboardResult.CapabilityCount> capabilityInvocationDistribution = new ArrayList<>();
    private List<OperationDashboardResult.DailyTokenCost> tokenCostTrend = new ArrayList<>();

    public static AiCostDashboardResult from(OperationDashboardResult source) {
        AiCostDashboardResult target = new AiCostDashboardResult();
        target.setPreset(source.getPreset());
        target.setRangeStart(source.getRangeStart());
        target.setRangeEnd(source.getRangeEnd());
        if (source.getSummary() != null) {
            target.setAiInvocationCount(source.getSummary().getAiInvocationCount());
            target.setTokenDeltaInRange(source.getSummary().getTokenDeltaInRange());
        }
        target.setCapabilityInvocationDistribution(source.getCapabilityInvocationDistribution());
        target.setTokenCostTrend(source.getTokenCostTrend());
        return target;
    }

    public static OperationDashboardQuery toQuery(AiCostDashboardQuery query) {
        OperationDashboardQuery target = new OperationDashboardQuery();
        target.setPreset(query.getPreset());
        target.setStartTime(query.getStartTime());
        target.setEndTime(query.getEndTime());
        return target;
    }
}
