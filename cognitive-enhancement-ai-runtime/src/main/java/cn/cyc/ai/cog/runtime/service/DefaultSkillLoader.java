package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.harness.SkillLoader;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SkillLoader 默认实现。
 *
 * @author cyc
 */
@Component
public class DefaultSkillLoader implements SkillLoader {

    /**
     * Skill 定义仓储。
     */
    private final SkillDefinitionRepository skillDefinitionRepository;

    /**
     * 构造默认技能装载器。
     *
     * @param skillDefinitionRepository Skill 定义仓储
     */
    public DefaultSkillLoader(SkillDefinitionRepository skillDefinitionRepository) {
        this.skillDefinitionRepository = skillDefinitionRepository;
    }

    /**
     * 为指定 Agent 加载其绑定的全部可用技能。
     *
     * @param agent 智能体定义
     * @return 排序后的技能定义列表
     */
    @Override
    public List<SkillDefinition> loadForAgent(AgentDefinition agent) {
        return agent.allowedSkillCodes().stream()
                .map(this::loadEnabled)
                .toList();
    }

    /**
     * 按编码列表批量加载技能定义。
     *
     * @param skillCodes 技能编码列表
     * @return 技能定义列表
     */
    @Override
    public List<SkillDefinition> loadByCodes(List<String> skillCodes) {
        return skillCodes.stream()
                .map(this::loadEnabled)
                .toList();
    }

    /**
     * 加载并验证启用状态的 Skill。
     *
     * @param skillCode Skill 编码
     * @return Skill 定义
     */
    private SkillDefinition loadEnabled(String skillCode) {
        SkillDefinition skill = skillDefinitionRepository.findByCode(skillCode)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到 Skill: " + skillCode));
        if (skill.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "Skill 未启用: " + skillCode);
        }
        return skill;
    }
}
