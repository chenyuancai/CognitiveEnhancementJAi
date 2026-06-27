package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class MessageTemplateSendRequest {

    private Long id;

    @NotBlank
    private String recipient;

    private Map<String, Object> params;
}
