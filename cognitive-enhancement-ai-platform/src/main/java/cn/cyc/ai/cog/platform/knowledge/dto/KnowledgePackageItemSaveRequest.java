package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KnowledgePackageItemSaveRequest {

    private Long id;

    private Long packageId;

    private Long parentId;
    private Long contentId;
    private String title;
    private Integer sortNo;
}
