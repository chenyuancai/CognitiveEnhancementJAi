package cn.cyc.ai.cog.core.metadata.agent;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Agent 定义对象。
 *
 * @author cyc
 */
public record AgentDefinition(
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
) implements MetadataDefinition {

    public AgentDefinition {
        agentCode = Objects.requireNonNull(agentCode, "agentCode 不能为空");
        agentName = Objects.requireNonNull(agentName, "agentName 不能为空");
        roleDesc = Objects.requireNonNull(roleDesc, "roleDesc 不能为空");
        goalDesc = Objects.requireNonNull(goalDesc, "goalDesc 不能为空");
        modelCode = Objects.requireNonNull(modelCode, "modelCode 不能为空");
        maxCost = Objects.requireNonNull(maxCost, "maxCost 不能为空");
        if (maxSteps <= 0) {
            throw new IllegalArgumentException("maxSteps 必须大于 0");
        }
        if (maxCost.signum() < 0) {
            throw new IllegalArgumentException("maxCost 不能小于 0");
        }
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs 必须大于 0");
        }
        allowedSkillCodes = List.copyOf(allowedSkillCodes == null ? List.of() : allowedSkillCodes);
        parameterConstraints = Map.copyOf(parameterConstraints == null ? Map.of() : parameterConstraints);
        status = Objects.requireNonNull(status, "status 不能为空");
    }

    @Override
    public String code() {
        return agentCode;
    }

    @Override
    public String name() {
        return agentName;
    }
}
