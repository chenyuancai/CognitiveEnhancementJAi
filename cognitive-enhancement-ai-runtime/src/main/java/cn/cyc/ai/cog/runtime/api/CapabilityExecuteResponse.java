package cn.cyc.ai.cog.runtime.api;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;

/**
 * 能力执行统一响应体。
 *
 * @param traceId    当前链路标识
 * @param capability 当前能力定义
 * @param agent      当前路由到的 Agent 定义
 * @param result     统一执行结果
 * @author cyc
 */
public record CapabilityExecuteResponse(
        String traceId,
        CapabilityDefinition capability,
        AgentDefinition agent,
        ExecutionResult result
) {
}
