package cn.cyc.ai.cog.core.metadata.skill;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

import java.util.List;
import java.util.Objects;

/**
 * Skill 定义对象。
 */
public record SkillDefinition(
        String skillCode,
        String skillName,
        String skillType,
        String skillInstruction,
        List<String> boundToolCodes,
        RiskLevel riskLevel,
        List<String> forbiddenRules,
        List<String> examples,
        CommonStatus status
) implements MetadataDefinition {

    public SkillDefinition {
        skillCode = Objects.requireNonNull(skillCode, "skillCode 不能为空");
        skillName = Objects.requireNonNull(skillName, "skillName 不能为空");
        skillType = Objects.requireNonNull(skillType, "skillType 不能为空");
        skillInstruction = Objects.requireNonNull(skillInstruction, "skillInstruction 不能为空");
        boundToolCodes = List.copyOf(boundToolCodes == null ? List.of() : boundToolCodes);
        riskLevel = Objects.requireNonNull(riskLevel, "riskLevel 不能为空");
        forbiddenRules = List.copyOf(forbiddenRules == null ? List.of() : forbiddenRules);
        examples = List.copyOf(examples == null ? List.of() : examples);
        status = Objects.requireNonNull(status, "status 不能为空");
    }

    @Override
    public String code() {
        return skillCode;
    }

    @Override
    public String name() {
        return skillName;
    }
}
