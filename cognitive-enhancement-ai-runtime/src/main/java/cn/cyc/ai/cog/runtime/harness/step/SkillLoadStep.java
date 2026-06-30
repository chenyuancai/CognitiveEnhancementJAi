package cn.cyc.ai.cog.runtime.harness.step;

import cn.cyc.ai.cog.core.harness.SkillLoader;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import cn.cyc.ai.cog.runtime.harness.support.HarnessImportWorkflowSupport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Skill 加载验证步骤，验证 Skill 被正确加载且约束被检查。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class SkillLoadStep implements HarnessStep {

    /** SkillLoader。 */
    private final SkillLoader skillLoader;
    /** 智能体仓储。 */
    private final AgentDefinitionRepository agentRepository;

    /**
     * 创建SkillLoadStep。
     *
     * @param skillLoader SkillLoader
     * @param agentRepository 智能体仓储
     */
    public SkillLoadStep(SkillLoader skillLoader, AgentDefinitionRepository agentRepository) {
        this.skillLoader = skillLoader;
        this.agentRepository = agentRepository;
    }

    /**
     * 执行step编码。
     * @return 执行结果
     */
    @Override
    public String stepCode() {
        return "SKILL_LOAD";
    }

    /**
     * 执行step名称。
     * @return 执行结果
     */
    @Override
    public String stepName() {
        return "Skill 加载验证";
    }

    /**
     * 执行描述。
     * @return 执行结果
     */
    @Override
    public String description() {
        return "验证 Skill 被正确加载，约束被检查";
    }

    /**
     * 执行操作。
     *
     * @param ctx ctx
     * @return 执行结果
     */
    @Override
    public HarnessStepResult run(HarnessContext ctx) {
        if (HarnessImportWorkflowSupport.isImportKbFileParse(ctx.scenario())) {
            return HarnessImportWorkflowSupport.skipStep(this, "导入工作流场景，跳过 Skill 加载");
        }
        if (ctx.scenario() == null || ctx.scenario().agentCode() == null) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, 0,
                    "场景配置为空", Map.of()
            );
        }

        try {
            AgentDefinition agent = agentRepository.findByCode(ctx.scenario().agentCode()).orElse(null);
            if (agent == null) {
                return new HarnessStepResult(
                        stepCode(), stepName(), false, 0,
                        "未找到 Agent: " + ctx.scenario().agentCode(), Map.of()
                );
            }

            List<SkillDefinition> skills = skillLoader.loadForAgent(agent);
            List<Map<String, Object>> skillInfos = new ArrayList<>();
            RiskLevel maxRiskLevel = RiskLevel.LOW;

            for (SkillDefinition skill : skills) {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("code", skill.skillCode());
                info.put("name", skill.skillName());
                info.put("status", skill.status().name());
                info.put("riskLevel", skill.riskLevel().name());
                info.put("boundTools", skill.boundToolCodes());
                info.put("forbiddenRules", skill.forbiddenRules());
                skillInfos.add(info);

                if (skill.riskLevel().ordinal() > maxRiskLevel.ordinal()) {
                    maxRiskLevel = skill.riskLevel();
                }
            }

            return new HarnessStepResult(
                    stepCode(), stepName(), true, 0,
                    "Skill 加载完成，风险评估: " + maxRiskLevel.name(),
                    Map.of("loadedSkills", skillInfos, "riskAssessment", maxRiskLevel.name())
            );
        } catch (Exception ex) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, 0,
                    "Skill 加载失败: " + ex.getMessage(),
                    Map.of("exception", ex.getClass().getSimpleName())
            );
        }
    }
}
