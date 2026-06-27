package cn.cyc.ai.cog.platform.membership.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_mbr_level")
public class MembershipLevelEntity extends BaseEntity {

    private String levelCode;
    private String levelName;
    private String segment;

    @TableField("is_default")
    private Boolean isDefault;

    private Integer sortNo;
    private String status;
    private String benefitsJson;
}
