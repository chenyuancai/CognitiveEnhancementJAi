package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import org.springframework.stereotype.Service;

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
                request.status()
        );
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
                definition.status()
        );
    }
}
