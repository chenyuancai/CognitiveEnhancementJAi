package cn.cyc.ai.cog.platform.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("qz_kb_knowledge_package_item")
public class KnowledgePackageItemEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long packageId;
    private Long parentId;
    private Long contentId;
    private String title;
    private Integer sortNo;
    private LocalDateTime createTime;
}
