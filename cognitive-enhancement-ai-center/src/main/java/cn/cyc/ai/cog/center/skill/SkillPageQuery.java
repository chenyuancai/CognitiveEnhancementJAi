package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.center.common.CenterPageQuery;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

/**
 * Skill 分页查询参数。
 *
 * @author cyc
 */
public class SkillPageQuery extends CenterPageQuery {

    /**
     * 技能类型。
     */
    private String skillType;

    /**
     * 风险等级。
     */
    private RiskLevel riskLevel;

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
}
