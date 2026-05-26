package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.util.Map;

/**
 * 能力定义返回对象。
 *
 * @param capabilityCode   能力编码
 * @param capabilityName   能力名称
 * @param capabilityDesc   能力描述
 * @param inputSchema      输入结构定义
 * @param outputSchema     输出结构定义
 * @param parameterConstraints 参数约束定义
 * @param executeMode      执行模式
 * @param boundAgentCode   绑定 Agent 编码
 * @param riskLevel        风险等级
 * @param needHumanConfirm 是否需要人工确认
 * @param status           启用状态
 * @author cyc
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
        CommonStatus status
) {
}
