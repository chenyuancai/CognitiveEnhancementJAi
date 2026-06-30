package cn.cyc.ai.cog.admin.workbench.dto;

import cn.cyc.ai.cog.admin.ai.dto.AiCostDashboardResult;
import cn.cyc.ai.cog.admin.ai.dto.AiRoutingOverviewResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作台聚合结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DashboardResult {

    /** range。 */
    private DateRangeVO range;
    /** overview。 */
    private DashboardOverview overview;
    /** trends。 */
    private List<TrendSeries> trends = new ArrayList<>();
    /** todo。 */
    private DashboardTodo todo;
    /** aiCost。 */
    private AiCostDashboardResult aiCost;
    /** aiRouting。 */
    private AiRoutingOverviewResult aiRouting;
}
