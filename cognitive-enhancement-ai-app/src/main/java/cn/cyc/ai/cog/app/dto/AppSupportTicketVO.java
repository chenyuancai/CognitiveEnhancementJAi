package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * C端支持Ticket视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppSupportTicketVO {

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

    /** 状态中文标签 */
    private String statusLabel;

    /** priority。 */
    private String priority;
    /** resolvedAt。 */
    private LocalDateTime resolvedAt;
    /** 创建时间 */
    private LocalDateTime createTime;
}
