package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupportTicketSaveRequest {

    private Long id;

    @NotBlank
    private String title;
    private String body;
    private String category;
    private String priority;
    private Long submitterUserId;
    private Long assigneeUserId;
}
