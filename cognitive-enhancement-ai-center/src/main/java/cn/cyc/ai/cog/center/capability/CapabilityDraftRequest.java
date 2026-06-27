package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.util.Map;

/**
 * 创建 Capability 草稿版本请求。
 *
 * @param version              新版本号，为空时自动递增 patch
 * @param capabilityName       名称
 * @param capabilityDesc         描述
 * @param inputSchema            输入 Schema
 * @param outputSchema           输出 Schema
 * @param parameterConstraints   参数约束
 * @param executeMode            执行模式
 * @param boundAgentCode         绑定 Agent
 * @param riskLevel              风险等级
 * @param needHumanConfirm       是否需人工确认
 * @author cyc
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
