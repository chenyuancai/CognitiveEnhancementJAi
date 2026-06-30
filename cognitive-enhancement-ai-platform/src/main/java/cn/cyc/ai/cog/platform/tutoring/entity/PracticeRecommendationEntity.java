package cn.cyc.ai.cog.platform.tutoring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 练习推荐实体（映射 qz_app_practice_recommendation）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_app_practice_recommendation")
public class PracticeRecommendationEntity {

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

    /** 会话 ID。 */
    /** 会话 ID */
    @TableField("session_id")
    private String sessionId;

    /** 追踪 ID，关联单次对话轮次。 */
    /** 链路 Trace ID */
    @TableField("trace_id")
    private String traceId;

    /** 关联知识点。 */
    /** 知识Point。 */
    @TableField("knowledge_point")
    private String knowledgePoint;

    /** 练习提示文本。 */
    /** 提示词Text。 */
    @TableField("prompt_text")
    private String promptText;

    /** 难度等级。 */
    private String difficulty;

    /** 状态：PENDING/COMPLETED 等。 */
    private String status;

    /** 创建时间。 */
    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
