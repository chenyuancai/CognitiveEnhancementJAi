package cn.cyc.ai.cog.center.agent.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Agent 定义数据库实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_agent_definition")
public class AgentDefinitionEntity extends BaseEntity {

    private Long tenantId;
    private String agentCode;
    private String agentName;
    private String roleDesc;
    private String goalDesc;
    private String modelCode;
    private Integer maxSteps;
    private BigDecimal maxCost;
    private Integer timeoutMs;
    private String parameterConstraints;
    private String status;
}
