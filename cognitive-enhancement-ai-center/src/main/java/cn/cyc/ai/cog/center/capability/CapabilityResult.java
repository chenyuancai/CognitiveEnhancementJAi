package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.time.Instant;
import java.util.Map;

/**
 * 能力定义返回对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityResult(
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
        CommonStatus status,
        String version,
        CapabilityLifecycleStatus lifecycleStatus,
        Instant publishedAt
) {
}
