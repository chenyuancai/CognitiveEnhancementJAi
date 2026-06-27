package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

import java.util.List;

/**
 * Skill DTO 定义。
 */
public final class SkillDtos {

    private SkillDtos() {
    }

    public record CreateRequest(
            String skillCode,
            String skillName,
            String skillType,
            String skillInstruction,
            List<String> boundToolCodes,
            RiskLevel riskLevel,
            List<String> forbiddenRules,
            List<String> examples,
            List<String> dependsOnSkillCodes,
            CommonStatus status
    ) {
    }

    public record UpdateRequest(
            String skillName,
            String skillType,
            String skillInstruction,
            List<String> boundToolCodes,
            RiskLevel riskLevel,
            List<String> forbiddenRules,
            List<String> examples,
            List<String> dependsOnSkillCodes,
            CommonStatus status
    ) {
    }

    public record Result(
            String skillCode,
            String skillName,
            String skillType,
            String skillInstruction,
            List<String> boundToolCodes,
            RiskLevel riskLevel,
            List<String> forbiddenRules,
            List<String> examples,
            List<String> dependsOnSkillCodes,
            CommonStatus status
    ) {
    }
}
