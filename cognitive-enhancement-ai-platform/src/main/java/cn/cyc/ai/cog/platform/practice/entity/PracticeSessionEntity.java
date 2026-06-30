package cn.cyc.ai.cog.platform.practice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 练习会话实体。
 */
@Data
@TableName("qz_app_practice_session")
public class PracticeSessionEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("user_id")
    private Long userId;

    @TableField("session_code")
    private String sessionCode;

    @TableField("source_content_id")
    private Long sourceContentId;

    private String title;

    @TableField("question_count")
    private Integer questionCount;

    @TableField("answered_count")
    private Integer answeredCount;

    private String status;

    private Integer accuracy;

    private Integer minutes;

    private String mode;

    @TableField("debrief_json")
    private String debriefJson;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
