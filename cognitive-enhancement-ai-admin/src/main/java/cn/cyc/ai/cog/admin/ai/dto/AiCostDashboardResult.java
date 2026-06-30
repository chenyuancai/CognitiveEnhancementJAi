package cn.cyc.ai.cog.admin.ai.dto;

import cn.cyc.ai.cog.admin.operation.dto.OperationDashboardQuery;
import cn.cyc.ai.cog.admin.operation.dto.OperationDashboardResult;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 成本看板（Admin 只读聚合）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AiCostDashboardResult {

    /** preset。 */
    private String preset;
    /** rangeStart。 */
    private LocalDateTime rangeStart;
    /** rangeEnd。 */
    private LocalDateTime rangeEnd;
    /** aiInvocation数量。 */
    private long aiInvocationCount;
    /** 令牌DeltaInRange。 */
    private long tokenDeltaInRange;
    /** 能力InvocationDistribution。 */
    private List<OperationDashboardResult.CapabilityCount> capabilityInvocationDistribution = new ArrayList<>();
    /** 令牌CostTrend。 */
    private List<OperationDashboardResult.DailyTokenCost> tokenCostTrend = new ArrayList<>();

    /**
     * 执行from。
     *
     * @param source 来源
     * @return 执行结果
     */
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

    /**
     * 转换为查询。
     *
     * @param query 查询
     * @return 转换结果
     */
    public static OperationDashboardQuery toQuery(AiCostDashboardQuery query) {
        OperationDashboardQuery target = new OperationDashboardQuery();
        target.setPreset(query.getPreset());
        target.setStartTime(query.getStartTime());
        target.setEndTime(query.getEndTime());
        return target;
    }
}
