package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Agent 定义写入请求。
 *
 * @param agentCode         Agent 编码
 * @param agentName         Agent 名称
 * @param roleDesc          角色描述
 * @param goalDesc          目标描述
 * @param modelCode         运行模型编码
 * @param maxSteps          最大步骤数
 * @param maxCost           最大成本
 * @param timeoutMs         超时时间
 * @param allowedSkillCodes 允许使用的 Skill 编码列表
 * @param parameterConstraints Agent 运行时参数约束
 * @param status            启用状态
 * @author cyc
 */
public record AgentUpsertRequest(
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
