package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupportTicketStatusUpdateRequest {

    private Long id;

    @NotBlank
    private String status;
    private Long assigneeUserId;
}
