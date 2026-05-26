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
 */
public final class AgentDtos {

    private AgentDtos() {
    }

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
