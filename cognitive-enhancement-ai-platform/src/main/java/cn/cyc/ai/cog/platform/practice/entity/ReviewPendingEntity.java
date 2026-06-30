package cn.cyc.ai.cog.platform.practice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待复习条目实体。
 */
@Data
@TableName("qz_app_review_pending")
public class ReviewPendingEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("user_id")
    private Long userId;

    @TableField("content_id")
    private Long contentId;

    private String title;

    private String tag;

    private Integer accuracy;

    @TableField("due_at")
    private LocalDateTime dueAt;

    private String urgency;

    private String status;

    @TableField("source_mistake_id")
    private Long sourceMistakeId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
