package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支持Ticket视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class SupportTicketVO {

    /** 主键 ID */
    private Long id;
    /** ticketNo。 */
    private String ticketNo;
    /** 标题。 */
    private String title;
    /** body。 */
    private String body;
    /** category。 */
    private String category;
    /** 状态。 */
    private String status;
    /** priority。 */
    private String priority;
    /** submitter用户ID */
    private Long submitterUserId;
    /** assignee用户ID */
    private Long assigneeUserId;
    /** resolvedAt。 */
    private LocalDateTime resolvedAt;
}
