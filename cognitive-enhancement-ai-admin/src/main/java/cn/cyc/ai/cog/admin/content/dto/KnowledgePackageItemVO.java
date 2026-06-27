package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

@Data
public class KnowledgePackageItemVO {

    private Long id;
    private Long packageId;
    private Long parentId;
    private Long contentId;
    private String title;
    private Integer sortNo;
}
