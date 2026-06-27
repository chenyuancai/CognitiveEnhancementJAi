package cn.cyc.ai.cog.platform.membership.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 等级权益值（映射 qz_mbr_level_benefit）。
 */
@Data
@TableName("qz_mbr_level_benefit")
public class LevelBenefitEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("level_id")
    private Long levelId;

    @TableField("benefit_code")
    private String benefitCode;

    @TableField("benefit_value")
    private String benefitValue;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
