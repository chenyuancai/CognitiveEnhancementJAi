package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

import java.util.List;

/**
 * Skill 定义写入请求。
 *
 * @param skillCode        Skill 编码
 * @param skillName        Skill 名称
 * @param skillType        Skill 类型
 * @param skillInstruction Skill 指令内容
 * @param boundToolCodes   绑定 Tool 编码列表
 * @param riskLevel        风险等级
 * @param forbiddenRules   禁止规则列表
 * @param examples         示例列表
 * @param status           启用状态
 * @author cyc
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
        CommonStatus status
) {
}
