package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

import java.util.List;

/**
 * Skill DTO 定义。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class SkillDtos {

    /**
     * 创建SkillDtos。
     */
    private SkillDtos() {
    }

    /**
     * 创建请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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

    /**
     * 更新请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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

    /**
     * Result 记录
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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
