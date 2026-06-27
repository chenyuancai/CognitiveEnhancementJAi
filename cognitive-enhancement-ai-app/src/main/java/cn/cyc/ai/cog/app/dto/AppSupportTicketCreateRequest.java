package cn.cyc.ai.cog.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppSupportTicketCreateRequest {

    @NotBlank
    private String title;
    private String body;
    private String category;
    private String priority;
}
