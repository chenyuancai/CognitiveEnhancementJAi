package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppSupportTicketVO {

    private Long id;
    private String ticketNo;
    private String title;
    private String body;
    private String category;
    private String status;
    private String priority;
    private LocalDateTime resolvedAt;
    private LocalDateTime createTime;
}
