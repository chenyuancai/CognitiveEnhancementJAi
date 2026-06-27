package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.center.common.ListResponse;
import cn.cyc.ai.cog.center.support.AbstractCenterMetadataService;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;

import java.util.List;

/**
 * Skill 管理服务。
 */
public class SkillCenterService extends AbstractCenterMetadataService<SkillDefinition> {

    public SkillCenterService(SkillDefinitionRepository repository) {
        super(repository, "Skill");
    }

    public ListResponse<SkillDtos.Result> list() {
        List<SkillDtos.Result> items = repository().listAll().stream().map(this::toResult).toList();
        return new ListResponse<>(items, items.size());
    }

    public SkillDtos.Result get(String skillCode) {
        return toResult(getRequired(skillCode));
    }

    public SkillDtos.Result create(SkillDtos.CreateRequest request) {
        ensureAbsent(request.skillCode());
        return toResult(save(new SkillDefinition(
                request.skillCode(),
                request.skillName(),
                request.skillType(),
                request.skillInstruction(),
                request.boundToolCodes(),
                request.riskLevel(),
                request.forbiddenRules(),
                request.examples(),
                normalizeDependsOn(request.dependsOnSkillCodes()),
                request.status()
        )));
    }

    public SkillDtos.Result update(String skillCode, SkillDtos.UpdateRequest request) {
        getRequired(skillCode);
        return toResult(save(new SkillDefinition(
                skillCode,
                request.skillName(),
                request.skillType(),
                request.skillInstruction(),
                request.boundToolCodes(),
                request.riskLevel(),
                request.forbiddenRules(),
                request.examples(),
                normalizeDependsOn(request.dependsOnSkillCodes()),
                request.status()
        )));
    }

    private SkillDtos.Result toResult(SkillDefinition definition) {
        return new SkillDtos.Result(
                definition.skillCode(),
                definition.skillName(),
                definition.skillType(),
                definition.skillInstruction(),
                definition.boundToolCodes(),
                definition.riskLevel(),
                definition.forbiddenRules(),
                definition.examples(),
                definition.dependsOnSkillCodes(),
                definition.status()
        );
    }

    private List<String> normalizeDependsOn(List<String> dependsOnSkillCodes) {
        return dependsOnSkillCodes == null ? List.of() : dependsOnSkillCodes;
    }
}
