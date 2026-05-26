package cn.cyc.ai.cog.runtime.domain;

import cn.cyc.ai.cog.runtime.api.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;

import java.util.List;
import java.util.Map;

/**
 * Runtime 主链路上下文。
 *
 * @param traceId    当前链路标识
 * @param request    原始执行请求
 * @param capability 已路由能力
 * @param agent      已装载 Agent
 * @param prompt     已解析 Prompt 模板
 * @param skills     已装载技能
 * @param attributes 扩展属性
 * @author cyc
 */
public record ExecutionContext(
        String traceId,
        CapabilityExecuteRequest request,
        CapabilityDefinition capability,
        AgentDefinition agent,
        PromptTemplate prompt,
        List<SkillDefinition> skills,
        Map<String, Object> attributes
) {

    /**
     * 构造运行时上下文并收敛集合类字段。
     */
    public ExecutionContext {
        skills = List.copyOf(skills == null ? List.of() : skills);
        attributes = Map.copyOf(attributes == null ? Map.of() : attributes);
    }

    /**
     * 基于当前上下文补齐 Agent、Prompt 与 Skill 装载结果。
     *
     * @param newAgent  新装载的 Agent
     * @param newPrompt 新装载的 Prompt
     * @param newSkills 新装载的 Skill 列表
     * @return 新上下文
     */
    public ExecutionContext withAgentPromptAndSkills(AgentDefinition newAgent,
                                                     PromptTemplate newPrompt,
                                                     List<SkillDefinition> newSkills) {
        return new ExecutionContext(traceId, request, capability, newAgent, newPrompt, newSkills, attributes);
    }
}
