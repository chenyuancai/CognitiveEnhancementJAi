package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.center.common.ListResponse;
import cn.cyc.ai.cog.center.support.AbstractCenterMetadataService;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;

import java.util.List;

/**
 * Skill 管理服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class SkillCenterService extends AbstractCenterMetadataService<SkillDefinition> {

    /**
     * 创建SkillCenter服务。
     *
     * @param repository 仓储
     */
    public SkillCenterService(SkillDefinitionRepository repository) {
        super(repository, "Skill");
    }

    /**
     * 查询Item列表。
     * @return 结果列表
     */
    public ListResponse<SkillDtos.Result> list() {
        List<SkillDtos.Result> items = repository().listAll().stream().map(this::toResult).toList();
        return new ListResponse<>(items, items.size());
    }

    /**
     * 执行get。
     *
     * @param skillCode Skill编码
     * @return 执行结果
     */
    public SkillDtos.Result get(String skillCode) {
        return toResult(getRequired(skillCode));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
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

    /**
     * 更新Item。
     *
     * @param skillCode Skill编码
     * @param request 请求
     * @return 更新结果
     */
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

    /**
     * 转换为结果。
     *
     * @param definition definition
     * @return 转换结果
     */
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

    /**
     * 执行normalizeDependsOn。
     *
     * @param dependsOnSkillCodes dependsOnSkillCodes
     * @return 执行结果
     */
    private List<String> normalizeDependsOn(List<String> dependsOnSkillCodes) {
        return dependsOnSkillCodes == null ? List.of() : dependsOnSkillCodes;
    }
}
