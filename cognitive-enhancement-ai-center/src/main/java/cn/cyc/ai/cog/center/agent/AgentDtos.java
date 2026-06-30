package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Agent DTO 定义。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class AgentDtos {

    /**
     * 创建AgentDtos。
     */
    private AgentDtos() {
    }

    /**
     * 创建请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record CreateRequest(
            String agentCode,
            String agentName,
            String roleDesc,
            String goalDesc,
            String modelCode,
            int maxSteps,
            BigDecimal maxCost,
            int timeoutMs,
            List<String> allowedSkillCodes,
            Map<String, ParameterConstraintDefinition> parameterConstraints,
            CommonStatus status
    ) {
    }

    /**
     * 更新请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record UpdateRequest(
            String agentName,
            String roleDesc,
            String goalDesc,
            String modelCode,
            int maxSteps,
            BigDecimal maxCost,
            int timeoutMs,
            List<String> allowedSkillCodes,
            Map<String, ParameterConstraintDefinition> parameterConstraints,
            CommonStatus status
    ) {
    }

    /**
     * Result 记录
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record Result(
            String agentCode,
            String agentName,
            String roleDesc,
            String goalDesc,
            String modelCode,
            int maxSteps,
            BigDecimal maxCost,
            int timeoutMs,
            List<String> allowedSkillCodes,
            Map<String, ParameterConstraintDefinition> parameterConstraints,
            CommonStatus status
    ) {
    }
}
