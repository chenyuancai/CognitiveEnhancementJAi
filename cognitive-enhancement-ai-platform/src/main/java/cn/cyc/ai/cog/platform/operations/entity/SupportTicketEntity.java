package cn.cyc.ai.cog.platform.operations.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ops_support_ticket")
public class SupportTicketEntity extends BaseEntity {

    @TableField("ticket_no")
    private String ticketNo;
    private String title;
    private String body;
    private String category;
    private String status;
    private String priority;
    @TableField("submitter_user_id")
    private Long submitterUserId;
    @TableField("assignee_user_id")
    private Long assigneeUserId;
    @TableField("resolved_at")
    private LocalDateTime resolvedAt;
}
