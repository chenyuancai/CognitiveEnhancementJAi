package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.common.SchemaDto;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

import java.util.Map;

/**
 * Capability DTO 定义。
 *
 * @author cyc
 */
public final class CapabilityDtos {

    private CapabilityDtos() {
    }

    public record CreateRequest(
            String capabilityCode,
            String capabilityName,
            String capabilityDesc,
            SchemaDto inputSchema,
            SchemaDto outputSchema,
            Map<String, ParameterConstraintDefinition> parameterConstraints,
            ExecutionMode executeMode,
            String boundAgentCode,
            RiskLevel riskLevel,
            boolean needHumanConfirm,
            CommonStatus status
    ) {
    }

    public record UpdateRequest(
            String capabilityName,
            String capabilityDesc,
            SchemaDto inputSchema,
            SchemaDto outputSchema,
            Map<String, ParameterConstraintDefinition> parameterConstraints,
            ExecutionMode executeMode,
            String boundAgentCode,
            RiskLevel riskLevel,
            boolean needHumanConfirm,
            CommonStatus status
    ) {
    }

    public record Result(
            String capabilityCode,
            String capabilityName,
            String capabilityDesc,
            SchemaDto inputSchema,
            SchemaDto outputSchema,
            Map<String, ParameterConstraintDefinition> parameterConstraints,
            ExecutionMode executeMode,
            String boundAgentCode,
            RiskLevel riskLevel,
            boolean needHumanConfirm,
            CommonStatus status
    ) {
    }
}
