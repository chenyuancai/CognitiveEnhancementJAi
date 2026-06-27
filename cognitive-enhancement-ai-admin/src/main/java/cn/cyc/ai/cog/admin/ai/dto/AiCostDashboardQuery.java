package cn.cyc.ai.cog.admin.ai.dto;

import lombok.Data;

/**
 * AI 成本看板查询参数。
 */
@Data
public class AiCostDashboardQuery {

    private String preset;
    private String startTime;
    private String endTime;
}
