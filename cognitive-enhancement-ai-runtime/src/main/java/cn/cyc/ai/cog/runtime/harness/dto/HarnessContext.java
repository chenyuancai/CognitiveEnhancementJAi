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
 * @param harnessId  Harness 执行唯一标识
 * @param traceId    链路追踪 ID
 * @param startTime  执行开始时间
 * @param scenario   前端选中的场景配置
 * @param capability 运行时解析出的能力定义
 * @param agent      运行时解析出的 Agent 定义
 * @param skills     运行时解析出的技能定义列表
 * @param tools      运行时解析出的工具定义列表
 * @param model      运行时解析出的模型定义
 * @param extra      步骤间传递数据
 * @author cyc
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
