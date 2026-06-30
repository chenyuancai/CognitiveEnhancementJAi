package cn.cyc.ai.cog.core.runtime;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;

/**
 * 能力执行统一响应体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityExecuteResponse(
        String traceId,
        CapabilityDefinition capability,
        AgentDefinition agent,
        ExecutionResult result
) {
}
