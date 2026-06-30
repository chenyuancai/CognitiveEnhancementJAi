package cn.cyc.ai.cog.admin.ai.dto;

import lombok.Data;

/**
 * AI 成本看板查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AiCostDashboardQuery {

    /** preset。 */
    private String preset;
    /** start时间。 */
    private String startTime;
    /** end时间。 */
    private String endTime;
}
