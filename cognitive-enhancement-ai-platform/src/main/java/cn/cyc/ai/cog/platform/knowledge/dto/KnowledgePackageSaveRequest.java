package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KnowledgePackageSaveRequest {

    private Long id;

    @NotBlank
    private String packageName;
    private String description;
    private String status;
}
