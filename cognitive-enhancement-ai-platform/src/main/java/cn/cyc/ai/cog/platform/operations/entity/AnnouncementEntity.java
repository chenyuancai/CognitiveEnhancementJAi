package cn.cyc.ai.cog.platform.operations.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ops_announcement")
public class AnnouncementEntity extends BaseEntity {

    private String title;
    private String body;
    private String status;
    @TableField("publish_at")
    private LocalDateTime publishAt;
    @TableField("target_level_codes")
    private String targetLevelCodes;
    @TableField("target_user_ids")
    private String targetUserIds;
}
