package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Skill 管理服务。
 *
 * @author cyc
 */
@Service
public class SkillAdminService extends AbstractMetadataAdminService<SkillDefinition, SkillUpsertRequest, SkillResult> {

    /**
     * 创建 Skill 后台管理服务。
     *
     * @param repository Skill 定义仓储
     */
    public SkillAdminService(SkillDefinitionRepository repository) {
        super(repository);
    }

    /**
     * 分页查询 Skill 定义。
     *
     * @param query 查询参数
     * @return 分页 Skill 列表
     */
    public CenterPageResult<SkillResult> listPage(SkillPageQuery query) {
        return listPage(
                query,
                definition -> matches(query.getSkillType(), definition.skillType())
                        && matches(query.getRiskLevel(), definition.riskLevel()),
                SkillDefinition::status,
                skillSorters()
        );
    }

    /**
     * 将 Skill 写入请求转换为 Skill 定义。
     *
     * @param request      Skill 写入请求
     * @param overrideCode 覆盖编码
     * @return Skill 定义
     */
    @Override
    protected SkillDefinition toDefinition(SkillUpsertRequest request, String overrideCode) {
        String skillCode = overrideCode != null ? overrideCode : Objects.requireNonNull(request.skillCode(), "skillCode 不能为空");
        return new SkillDefinition(
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
        );
    }

    private List<String> normalizeDependsOn(List<String> dependsOnSkillCodes) {
        return dependsOnSkillCodes == null ? List.of() : dependsOnSkillCodes;
    }

    /**
     * 将 Skill 定义转换为返回对象。
     *
     * @param definition Skill 定义
     * @return Skill 返回对象
     */
    @Override
    protected SkillResult toResult(SkillDefinition definition) {
        return new SkillResult(
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

    private Map<String, Comparator<SkillDefinition>> skillSorters() {
        Map<String, Comparator<SkillDefinition>> sorters = new LinkedHashMap<>(commonSorters(SkillDefinition::status));
        sorters.put("skillType", Comparator.comparing(SkillDefinition::skillType));
        sorters.put("riskLevel", Comparator.comparing(definition -> definition.riskLevel().name()));
        return sorters;
    }

    private boolean matches(String expected, String actual) {
        return !StringUtils.hasText(expected) || expected.equals(actual);
    }

    private boolean matches(RiskLevel expected, RiskLevel actual) {
        return expected == null || expected == actual;
    }
}
