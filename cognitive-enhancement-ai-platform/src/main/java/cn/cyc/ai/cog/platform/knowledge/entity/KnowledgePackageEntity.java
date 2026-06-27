package cn.cyc.ai.cog.platform.knowledge.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_kb_knowledge_package")
public class KnowledgePackageEntity extends BaseEntity {

    private String packageName;
    private String description;
    private String status;
}
