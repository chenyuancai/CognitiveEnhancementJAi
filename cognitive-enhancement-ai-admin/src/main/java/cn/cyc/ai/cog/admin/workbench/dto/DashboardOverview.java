package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作台概览卡片。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DashboardOverview {

    /** 用户总数。 */
    private long userTotal;
    /** 用户TodayNew。 */
    private long userTodayNew;
    /** activeUsers。 */
    private long activeUsers;
    /** paidMembers。 */
    private long paidMembers;
    /** member等级Dist。 */
    private List<LevelCountItem> memberLevelDist = new ArrayList<>();
    /** revenueToday。 */
    private long revenueToday;
    /** revenueMonth。 */
    private long revenueMonth;
    /** 订单总数。 */
    private long orderTotal;
    /** 订单Pending。 */
    private long orderPending;
    /** 内容总数。 */
    private long contentTotal;
    /** 内容Pending。 */
    private long contentPending;
    /** ai令牌Today。 */
    private long aiTokenToday;
    /** aiCostToday。 */
    private long aiCostToday;
}
