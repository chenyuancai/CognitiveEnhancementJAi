package cn.cyc.ai.cog.platform.tutoring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错题记录实体（映射 qz_app_mistake_record）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_app_mistake_record")
public class MistakeRecordEntity {

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

    /** 关联内容 ID（练习错题）。 */
    @TableField("content_id")
    private Long contentId;

    /** 得分。 */
    private Integer score;

    /** 标签。 */
    private String tag;

    /** 来源：TUTORING / PRACTICE。 */
    @TableField("source_type")
    private String sourceType;

    /** 错题摘要描述。 */
    /** 错题摘要。 */
    @TableField("mistake_summary")
    private String mistakeSummary;

    /** 学生解题思路。 */
    /** 用户Approach。 */
    @TableField("user_approach")
    private String userApproach;

    /** 纠正提示。 */
    /** correctionHint。 */
    @TableField("correction_hint")
    private String correctionHint;

    /** 状态：OPEN/RESOLVED 等。 */
    private String status;

    /** 创建时间。 */
    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
