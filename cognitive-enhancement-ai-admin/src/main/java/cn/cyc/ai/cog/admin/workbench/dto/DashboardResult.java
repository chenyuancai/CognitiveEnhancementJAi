package cn.cyc.ai.cog.admin.workbench.dto;

import cn.cyc.ai.cog.admin.ai.dto.AiCostDashboardResult;
import cn.cyc.ai.cog.admin.ai.dto.AiRoutingOverviewResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作台聚合结果。
 */
@Data
public class DashboardResult {

    private DateRangeVO range;
    private DashboardOverview overview;
    private List<TrendSeries> trends = new ArrayList<>();
    private DashboardTodo todo;
    private AiCostDashboardResult aiCost;
    private AiRoutingOverviewResult aiRouting;
}
