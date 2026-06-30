package cn.cyc.ai.cog.platform.tutoring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习画像实体（映射 qz_app_learning_profile）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_app_learning_profile")
public class LearningProfileEntity {

    /** 主键 ID。 */
    /** 主键 ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 租户 ID。 */
    /** 租户 ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 用户 ID。 */
    /** 用户 ID */
    @TableField("user_id")
    private Long userId;

    /** 学习画像 JSON 内容。 */
    /** 画像 JSON 内容 */
    @TableField("profile_json")
    private String profileJson;

    /** 版本号，每次更新递增。 */
    /** 版本号，每次更新递增 */
    @TableField("version_no")
    private Integer versionNo;

    /** 创建时间。 */
    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;

    /** 更新时间。 */
    /** 更新时间 */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
