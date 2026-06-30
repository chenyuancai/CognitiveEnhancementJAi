package cn.cyc.ai.cog.platform.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识PackageItem实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_kb_knowledge_package_item")
public class KnowledgePackageItemEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
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
    /** 创建时间 */
    private LocalDateTime createTime;
}
