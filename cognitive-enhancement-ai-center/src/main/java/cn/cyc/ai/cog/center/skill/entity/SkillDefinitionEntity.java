package cn.cyc.ai.cog.center.skill.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 技能定义数据库实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_skill_definition")
public class SkillDefinitionEntity extends BaseEntity {

    private Long tenantId;
    private String skillCode;
    private String skillName;
    private String skillType;
    private String skillInstruction;
    private String riskLevel;
    private String forbiddenRules;
    private String examples;
    private String dependsOnSkillCodes;
    private String status;
}
