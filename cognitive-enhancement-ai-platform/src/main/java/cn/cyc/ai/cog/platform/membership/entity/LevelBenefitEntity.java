package cn.cyc.ai.cog.platform.membership.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 等级权益值（映射 qz_mbr_level_benefit）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_mbr_level_benefit")
public class LevelBenefitEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户 ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 等级ID */
    @TableField("level_id")
    private Long levelId;

    /** 权益编码。 */
    @TableField("benefit_code")
    private String benefitCode;

    /** 权益值。 */
    @TableField("benefit_value")
    private String benefitValue;

    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
