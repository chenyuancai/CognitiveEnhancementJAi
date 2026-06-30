package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Agent 定义写入请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
