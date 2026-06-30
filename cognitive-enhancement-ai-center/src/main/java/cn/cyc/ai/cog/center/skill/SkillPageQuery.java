package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.center.common.CenterPageQuery;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

/**
 * Skill 分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 获取Skill类型。
     * @return Skill类型
     */
    public String getSkillType() {
        return skillType;
    }

    /**
     * 设置Skill类型。
     *
     * @param skillType Skill类型
     */
    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    /**
     * 获取Risk等级。
     * @return Risk等级
     */
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    /**
     * 设置Risk等级。
     *
     * @param riskLevel risk等级
     */
    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
}
