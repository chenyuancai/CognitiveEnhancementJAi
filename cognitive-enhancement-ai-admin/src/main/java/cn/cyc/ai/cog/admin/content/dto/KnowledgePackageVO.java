package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

@Data
public class KnowledgePackageVO {

    private Long id;
    private String packageName;
    private String description;
    private String status;
}
