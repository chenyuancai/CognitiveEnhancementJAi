package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.util.Map;

/**
 * 创建 Capability 草稿版本请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityDraftRequest(String capabilityCode, 
        String version,
        String capabilityName,
        String capabilityDesc,
        SchemaDefinition inputSchema,
        SchemaDefinition outputSchema,
        Map<String, ParameterConstraintDefinition> parameterConstraints,
        ExecutionMode executeMode,
        String boundAgentCode,
        RiskLevel riskLevel,
        Boolean needHumanConfirm
) {
}
