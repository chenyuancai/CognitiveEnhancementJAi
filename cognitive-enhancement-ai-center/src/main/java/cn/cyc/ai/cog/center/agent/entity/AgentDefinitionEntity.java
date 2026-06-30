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
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_agent_definition")
public class AgentDefinitionEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 智能体编码。 */
    private String agentCode;
    /** 智能体名称。 */
    private String agentName;
    /** 角色Desc。 */
    private String roleDesc;
    /** goalDesc。 */
    private String goalDesc;
    /** 模型编码。 */
    private String modelCode;
    /** maxSteps。 */
    private Integer maxSteps;
    /** maxCost。 */
    private BigDecimal maxCost;
    /** timeoutMs。 */
    private Integer timeoutMs;
    /** parameterConstraints。 */
    private String parameterConstraints;
    /** 状态。 */
    private String status;
}
