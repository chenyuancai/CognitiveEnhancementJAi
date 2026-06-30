package cn.cyc.ai.cog.platform.tutoring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息引用实体（映射 qz_app_message_reference）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_app_message_reference")
public class MessageReferenceEntity {

    /** 主键 ID。 */
    /** 主键 ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    /** 引用类型，如知识点、文档片段等。 */
    /** ref类型。 */
    @TableField("ref_type")
    private String refType;

    /** 引用目标 ID。 */
    /** refID */
    @TableField("ref_id")
    private String refId;

    /** 引用摘录文本。 */
    private String excerpt;

    /** 创建时间。 */
    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
