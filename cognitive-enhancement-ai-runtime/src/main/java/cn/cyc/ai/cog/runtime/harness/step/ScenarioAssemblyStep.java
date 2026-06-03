package cn.cyc.ai.cog.runtime.harness.step;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景装配解析步骤，将前端编码解析为运行时对象并验证链路一致性。
 *
 * @author cyc
 */
@Component
public class ScenarioAssemblyStep implements HarnessStep {

    private final AgentDefinitionRepository agentRepository;
    private final CapabilityDefinitionRepository capabilityRepository;
    private final SkillDefinitionRepository skillRepository;
    private final ToolDefinitionRepository toolRepository;
    private final ModelDefinitionRepository modelRepository;

    public ScenarioAssemblyStep(AgentDefinitionRepository agentRepository,
                                 CapabilityDefinitionRepository capabilityRepository,
                                 SkillDefinitionRepository skillRepository,
                                 ToolDefinitionRepository toolRepository,
                                 ModelDefinitionRepository modelRepository) {
        this.agentRepository = agentRepository;
        this.capabilityRepository = capabilityRepository;
        this.skillRepository = skillRepository;
        this.toolRepository = toolRepository;
        this.modelRepository = modelRepository;
    }

    @Override
    public String stepCode() {
        return "SCENARIO_ASSEMBLY";
    }

    @Override
    public String stepName() {
        return "场景装配解析";
    }

    @Override
    public String description() {
        return "将前端编码解析为运行时对象并验证链路一致性";
    }

    @Override
    public HarnessStepResult run(HarnessContext ctx) {
        HarnessScenario scenario = ctx.scenario();
        if (scenario == null || scenario.agentCode() == null) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, 0,
                    "场景配置为空或 agentCode 未指定", Map.of()
            );
        }

        try {
            AgentDefinition agent = agentRepository.findByCode(scenario.agentCode())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到 Agent: " + scenario.agentCode()));

            CapabilityDefinition capability = resolveCapability(scenario, agent);
            List<SkillDefinition> skills = resolveSkills(scenario, agent);
            List<ToolDefinition> tools = resolveTools(scenario, skills);
            ModelDefinition model = resolveModel(scenario, agent);

            Map<String, Object> resolved = new LinkedHashMap<>();
            resolved.put("resolvedAgent", agent.agentCode());
            resolved.put("resolvedCapability", capability != null ? capability.capabilityCode() : null);
            resolved.put("resolvedSkills", skills.stream().map(SkillDefinition::skillCode).toList());
            resolved.put("resolvedTools", tools.stream().map(ToolDefinition::toolCode).toList());
            resolved.put("resolvedModel", model != null ? model.modelCode() : null);

            return new HarnessStepResult(
                    stepCode(), stepName(), true, 0,
                    "场景装配成功", resolved
            );
        } catch (BusinessException ex) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, 0,
                    "场景装配失败: " + ex.getMessage(), Map.of("errorCode", ex.getCode())
            );
        }
    }

    private CapabilityDefinition resolveCapability(HarnessScenario scenario, AgentDefinition agent) {
        if (scenario.capabilityCode() != null) {
            return capabilityRepository.findByCode(scenario.capabilityCode())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到 Capability: " + scenario.capabilityCode()));
        }
        return null;
    }

    private List<SkillDefinition> resolveSkills(HarnessScenario scenario, AgentDefinition agent) {
        List<String> codes = scenario.skillCodes() != null ? scenario.skillCodes() : agent.allowedSkillCodes();
        List<SkillDefinition> result = new ArrayList<>();
        for (String code : codes) {
            skillRepository.findByCode(code).ifPresent(result::add);
        }
        return result;
    }

    private List<ToolDefinition> resolveTools(HarnessScenario scenario, List<SkillDefinition> skills) {
        List<String> codes = scenario.toolCodes();
        if (codes == null) {
            codes = skills.stream()
                    .flatMap(s -> s.boundToolCodes().stream())
                    .distinct()
                    .toList();
        }
        List<ToolDefinition> result = new ArrayList<>();
        for (String code : codes) {
            toolRepository.findByCode(code).ifPresent(result::add);
        }
        return result;
    }

    private ModelDefinition resolveModel(HarnessScenario scenario, AgentDefinition agent) {
        String modelCode = scenario.modelCode() != null ? scenario.modelCode() : agent.modelCode();
        return modelRepository.findByCode(modelCode)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到模型: " + modelCode));
    }
}
