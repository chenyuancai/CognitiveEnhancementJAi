package cn.cyc.ai.cog.runtime.skill.loader;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.harness.SkillLoader;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * SkillLoader 默认实现，支持 Skill 依赖解析与去重。
 *
 * @author cyc
 */
@Component
public class DefaultSkillLoader implements SkillLoader {

    private final SkillDefinitionRepository skillDefinitionRepository;

    public DefaultSkillLoader(SkillDefinitionRepository skillDefinitionRepository) {
        this.skillDefinitionRepository = skillDefinitionRepository;
    }

    @Override
    public List<SkillDefinition> loadForAgent(AgentDefinition agent) {
        return resolveWithDependencies(agent.allowedSkillCodes());
    }

    @Override
    public List<SkillDefinition> loadByCodes(List<String> skillCodes) {
        return resolveWithDependencies(skillCodes);
    }

    private List<SkillDefinition> resolveWithDependencies(List<String> rootCodes) {
        if (rootCodes == null || rootCodes.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<String> orderedCodes = new LinkedHashSet<>();
        Set<String> visiting = new HashSet<>();
        for (String skillCode : rootCodes) {
            visitSkill(skillCode, orderedCodes, visiting);
        }
        List<SkillDefinition> loaded = new ArrayList<>(orderedCodes.size());
        for (String skillCode : orderedCodes) {
            loaded.add(loadEnabled(skillCode));
        }
        return List.copyOf(loaded);
    }

    private void visitSkill(String skillCode, LinkedHashSet<String> orderedCodes, Set<String> visiting) {
        if (orderedCodes.contains(skillCode)) {
            return;
        }
        if (!visiting.add(skillCode)) {
            throw new BusinessException("INVALID_ARGUMENT", "Skill 依赖存在循环: " + skillCode);
        }
        SkillDefinition skill = loadEnabled(skillCode);
        for (String dependencyCode : skill.dependsOnSkillCodes()) {
            visitSkill(dependencyCode, orderedCodes, visiting);
        }
        visiting.remove(skillCode);
        orderedCodes.add(skillCode);
    }

    private SkillDefinition loadEnabled(String skillCode) {
        SkillDefinition skill = skillDefinitionRepository.findByCode(skillCode)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到 Skill: " + skillCode));
        if (skill.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "Skill 未启用: " + skillCode);
        }
        return skill;
    }
}
