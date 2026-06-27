package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageTemplateSaveRequest {

    private Long id;

    @NotBlank
    private String templateCode;
    @NotBlank
    private String templateName;
    @NotBlank
    private String channel;
    @NotBlank
    private String content;
    private String variableSchema;
    private String status;
}
