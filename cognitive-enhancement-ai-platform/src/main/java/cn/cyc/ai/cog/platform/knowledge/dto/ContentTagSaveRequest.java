package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContentTagSaveRequest {

    private Long id;

    @NotBlank
    private String tagName;
    private String tagColor;
}
