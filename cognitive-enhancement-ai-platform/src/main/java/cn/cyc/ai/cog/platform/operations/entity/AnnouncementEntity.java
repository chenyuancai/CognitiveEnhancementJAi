package cn.cyc.ai.cog.platform.operations.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Announcement实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ops_announcement")
public class AnnouncementEntity extends BaseEntity {

    /** 标题。 */
    private String title;
    /** body。 */
    private String body;
    /** 状态。 */
    private String status;
    /** publishAt。 */
    @TableField("publish_at")
    private LocalDateTime publishAt;
    /** 目标等级Codes。 */
    @TableField("target_level_codes")
    private String targetLevelCodes;
    /** 目标用户Ids。 */
    @TableField("target_user_ids")
    private String targetUserIds;
}
