package cn.cyc.ai.cog.core.metadata.capability;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.util.Map;
import java.util.Objects;

/**
 * 能力定义对象。
 *
 * @author cyc
 */
public record CapabilityDefinition(
        String capabilityCode,
        String capabilityName,
        String capabilityDesc,
        SchemaDefinition inputSchema,
        SchemaDefinition outputSchema,
        Map<String, ParameterConstraintDefinition> parameterConstraints,
        ExecutionMode executeMode,
        String boundAgentCode,
        RiskLevel riskLevel,
        boolean needHumanConfirm,
        CommonStatus status
) implements MetadataDefinition {

    public CapabilityDefinition {
        capabilityCode = Objects.requireNonNull(capabilityCode, "capabilityCode 不能为空");
        capabilityName = Objects.requireNonNull(capabilityName, "capabilityName 不能为空");
        capabilityDesc = Objects.requireNonNull(capabilityDesc, "capabilityDesc 不能为空");
        inputSchema = Objects.requireNonNull(inputSchema, "inputSchema 不能为空");
        outputSchema = Objects.requireNonNull(outputSchema, "outputSchema 不能为空");
        parameterConstraints = Map.copyOf(parameterConstraints == null ? Map.of() : parameterConstraints);
        executeMode = Objects.requireNonNull(executeMode, "executeMode 不能为空");
        boundAgentCode = Objects.requireNonNull(boundAgentCode, "boundAgentCode 不能为空");
        riskLevel = Objects.requireNonNull(riskLevel, "riskLevel 不能为空");
        status = Objects.requireNonNull(status, "status 不能为空");
    }

    @Override
    public String code() {
        return capabilityCode;
    }

    @Override
    public String name() {
        return capabilityName;
    }
}
