package cn.cyc.ai.cog.runtime.knowledge.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 场景知识绑定实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_scenario_knowledge_binding")
public class ScenarioKnowledgeBindingEntity extends BaseEntity {

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 绑定 ID。
     */
    private String bindingId;

    /**
     * 场景编码。
     */
    private String scenarioCode;

    /**
     * 知识库编码。
     */
    private String knowledgeCode;

    /**
     * 优先级。
     */
    private Integer priority;

    /**
     * 是否启用。
     */
    private Boolean enabled;

    /**
     * 记录时间。
     */
    private Instant recordedAt;
}
