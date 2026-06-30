package cn.cyc.ai.cog.platform.tutoring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习计划实体（映射 qz_app_learning_plan）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_app_learning_plan")
public class LearningPlanEntity {

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

    /** 计划标题。 */
    /** 计划标题。 */
    @TableField("plan_title")
    private String planTitle;

    /** 计划内容 JSON。 */
    /** 计划JSON。 */
    @TableField("plan_json")
    private String planJson;

    /** 状态：ACTIVE/ARCHIVED 等。 */
    private String status;

    /** 创建时间。 */
    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;

    /** 更新时间。 */
    /** 更新时间 */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
