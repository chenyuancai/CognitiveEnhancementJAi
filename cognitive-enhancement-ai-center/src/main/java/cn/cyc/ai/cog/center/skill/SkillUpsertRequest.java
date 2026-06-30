package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

import java.util.List;

/**
 * Skill 定义写入请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record SkillUpsertRequest(
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
