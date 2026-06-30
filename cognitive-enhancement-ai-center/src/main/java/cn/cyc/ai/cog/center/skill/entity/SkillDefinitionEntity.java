package cn.cyc.ai.cog.center.skill.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 技能定义数据库实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_skill_definition")
public class SkillDefinitionEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** Skill编码。 */
    private String skillCode;
    /** Skill名称。 */
    private String skillName;
    /** Skill类型。 */
    private String skillType;
    /** SkillInstruction。 */
    private String skillInstruction;
    /** risk等级。 */
    private String riskLevel;
    /** forbiddenRules。 */
    private String forbiddenRules;
    /** examples。 */
    private String examples;
    /** dependsOnSkillCodes。 */
    private String dependsOnSkillCodes;
    /** 状态。 */
    private String status;
}
