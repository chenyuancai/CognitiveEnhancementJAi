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
 * @date 2026/6/15 14:18
 */
public final class CapabilityDtos {

    /**
     * 创建CapabilityDtos。
     */
    private CapabilityDtos() {
    }

    /**
     * 创建请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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

    /**
     * 更新请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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

    /**
     * Result 记录
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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
