package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 工作台待办/告警汇总。
 */
@Data
public class DashboardTodo {

    private long pendingAudit;
    private long pendingOrder;
    private long failedImport;
    private long expiringMembers;
    private long aiAlerts;
    private long pendingTickets;
}
