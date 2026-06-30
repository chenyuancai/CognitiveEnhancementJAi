package cn.cyc.ai.cog.platform.knowledge.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识Package实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_kb_knowledge_package")
public class KnowledgePackageEntity extends BaseEntity {

    /** package名称。 */
    private String packageName;
    /** 描述。 */
    private String description;
    /** 状态。 */
    private String status;
}
