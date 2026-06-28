package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.runtime.skill.loader.DefaultSkillLoader;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSkillLoaderTest {

    @Test
    void loadForAgent_shouldReturnEnabledSkills() {
        SkillDefinitionRepository repo = mock(SkillDefinitionRepository.class);
        SkillDefinition skill = skill("skill.test", List.of());
        when(repo.findByCode("skill.test")).thenReturn(Optional.of(skill));

        AgentDefinition agent = agent(List.of("skill.test"));

        DefaultSkillLoader loader = new DefaultSkillLoader(repo);
        List<SkillDefinition> result = loader.loadForAgent(agent);

        assertEquals(1, result.size());
        assertEquals("skill.test", result.get(0).skillCode());
    }

    @Test
    void shouldResolveSkillDependenciesBeforeRootSkill() {
        SkillDefinitionRepository repo = mock(SkillDefinitionRepository.class);
        when(repo.findByCode("skill.base")).thenReturn(Optional.of(skill("skill.base", List.of())));
        when(repo.findByCode("skill.advanced")).thenReturn(Optional.of(
                skill("skill.advanced", List.of("skill.base"))));

        AgentDefinition agent = agent(List.of("skill.advanced"));

        List<SkillDefinition> result = new DefaultSkillLoader(repo).loadForAgent(agent);

        assertEquals(2, result.size());
        assertEquals("skill.base", result.get(0).skillCode());
        assertEquals("skill.advanced", result.get(1).skillCode());
    }

    @Test
    void shouldRejectCircularSkillDependencies() {
        SkillDefinitionRepository repo = mock(SkillDefinitionRepository.class);
        when(repo.findByCode("skill.a")).thenReturn(Optional.of(skill("skill.a", List.of("skill.b"))));
        when(repo.findByCode("skill.b")).thenReturn(Optional.of(skill("skill.b", List.of("skill.a"))));

        AgentDefinition agent = agent(List.of("skill.a"));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> new DefaultSkillLoader(repo).loadForAgent(agent));
        assertEquals("INVALID_ARGUMENT", exception.getSemanticCode());
    }

    private SkillDefinition skill(String skillCode, List<String> dependsOnSkillCodes) {
        return new SkillDefinition(
                skillCode, "测试技能", "QA", "测试指令",
                List.of(), RiskLevel.LOW,
                List.of(), List.of(), dependsOnSkillCodes, CommonStatus.ENABLED
        );
    }

    private AgentDefinition agent(List<String> allowedSkillCodes) {
        return new AgentDefinition(
                "agent.qa", "问答 Agent", "role", "goal", "gpt-4o-mini",
                4, BigDecimal.ONE, 20000, allowedSkillCodes, java.util.Map.of(), CommonStatus.ENABLED
        );
    }
}
