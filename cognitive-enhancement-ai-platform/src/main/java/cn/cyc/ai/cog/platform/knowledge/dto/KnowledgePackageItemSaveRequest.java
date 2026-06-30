package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 知识PackageItemSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class KnowledgePackageItemSaveRequest {

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
