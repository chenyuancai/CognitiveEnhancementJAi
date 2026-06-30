package cn.cyc.ai.cog.platform.tutoring.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 辅导蓝图实体（映射 qz_app_tutoring_blueprint）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_app_tutoring_blueprint")
public class TutoringBlueprintEntity extends BaseEntity {

    /** 租户 ID。 */
    /** 租户 ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 会话 ID。 */
    /** 会话 ID */
    @TableField("session_id")
    private String sessionId;

    /** 追踪 ID，关联单次对话轮次。 */
    /** 链路 Trace ID */
    @TableField("trace_id")
    private String traceId;

    /** 消息 ID。 */
    /** 消息 ID */
    @TableField("message_id")
    private String messageId;

    /** 学生意图分类。 */
    private String intent;

    /** 教学策略。 */
    private String strategy;

    /** 辅导蓝图 JSON 内容。 */
    /** 蓝图JSON。 */
    @TableField("blueprint_json")
    private String blueprintJson;
}
