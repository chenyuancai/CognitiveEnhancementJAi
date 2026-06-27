package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作台概览卡片。
 */
@Data
public class DashboardOverview {

    private long userTotal;
    private long userTodayNew;
    private long activeUsers;
    private long paidMembers;
    private List<LevelCountItem> memberLevelDist = new ArrayList<>();
    private long revenueToday;
    private long revenueMonth;
    private long orderTotal;
    private long orderPending;
    private long contentTotal;
    private long contentPending;
    private long aiTokenToday;
    private long aiCostToday;
}
