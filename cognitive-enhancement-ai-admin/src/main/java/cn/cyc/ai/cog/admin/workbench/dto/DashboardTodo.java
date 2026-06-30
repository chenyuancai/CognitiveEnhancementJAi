package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 工作台待办/告警汇总。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DashboardTodo {

    /** pendingAudit。 */
    private long pendingAudit;
    /** pending订单。 */
    private long pendingOrder;
    /** failedImport。 */
    private long failedImport;
    /** expiringMembers。 */
    private long expiringMembers;
    /** aiAlerts。 */
    private long aiAlerts;
    /** pendingTickets。 */
    private long pendingTickets;
}
