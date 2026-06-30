package cn.cyc.ai.cog.platform.operations.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 支持Ticket实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ops_support_ticket")
public class SupportTicketEntity extends BaseEntity {

    /** ticketNo。 */
    @TableField("ticket_no")
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
    @TableField("submitter_user_id")
    private Long submitterUserId;
    /** assignee用户ID */
    @TableField("assignee_user_id")
    private Long assigneeUserId;
    /** resolvedAt。 */
    @TableField("resolved_at")
    private LocalDateTime resolvedAt;
}
