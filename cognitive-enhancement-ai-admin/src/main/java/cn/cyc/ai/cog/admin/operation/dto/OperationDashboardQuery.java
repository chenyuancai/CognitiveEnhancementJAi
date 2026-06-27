package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

/**
 * 运营看板查询参数。
 */
@Data
public class OperationDashboardQuery {

    /** 预设范围：TODAY / LAST_7_DAYS / LAST_30_DAYS；与 startTime/endTime 互斥时优先 preset。 */
    private String preset = "LAST_7_DAYS";

    private String startTime;

    private String endTime;
}
