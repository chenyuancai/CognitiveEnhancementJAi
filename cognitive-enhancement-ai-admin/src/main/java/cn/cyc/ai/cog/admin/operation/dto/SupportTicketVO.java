package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupportTicketVO {

    private Long id;
    private String ticketNo;
    private String title;
    private String body;
    private String category;
    private String status;
    private String priority;
    private Long submitterUserId;
    private Long assigneeUserId;
    private LocalDateTime resolvedAt;
}
