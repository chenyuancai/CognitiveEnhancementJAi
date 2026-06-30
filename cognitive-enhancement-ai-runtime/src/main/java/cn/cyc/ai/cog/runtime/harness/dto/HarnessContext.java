package cn.cyc.ai.cog.runtime.harness.dto;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Harness 执行上下文，跨步骤共享状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record HarnessContext(
        String harnessId,
        String traceId,
        Instant startTime,
        HarnessScenario scenario,
        CapabilityDefinition capability,
        AgentDefinition agent,
        List<SkillDefinition> skills,
        List<ToolDefinition> tools,
        ModelDefinition model,
        Map<String, Object> extra
) {
}
