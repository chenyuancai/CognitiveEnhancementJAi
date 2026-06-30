package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

/**
 * 知识PackageItem视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class KnowledgePackageItemVO {

    /** 主键 ID */
    private Long id;
    /** packageID */
    private Long packageId;
    /** parentID */
    private Long parentId;
    /** 内容ID */
    private Long contentId;
    /** 标题。 */
    private String title;
    /** sortNo。 */
    private Integer sortNo;
}
