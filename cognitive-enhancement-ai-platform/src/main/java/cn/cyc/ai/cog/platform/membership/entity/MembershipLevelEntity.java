package cn.cyc.ai.cog.platform.membership.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员等级实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_mbr_level")
public class MembershipLevelEntity extends BaseEntity {

    /** 等级编码。 */
    private String levelCode;
    /** 等级名称。 */
    private String levelName;
    /** segment。 */
    private String segment;

    /** is默认。 */
    @TableField("is_default")
    private Boolean isDefault;

    /** sortNo。 */
    private Integer sortNo;
    /** 状态。 */
    private String status;
    /** benefitsJSON。 */
    private String benefitsJson;
}
