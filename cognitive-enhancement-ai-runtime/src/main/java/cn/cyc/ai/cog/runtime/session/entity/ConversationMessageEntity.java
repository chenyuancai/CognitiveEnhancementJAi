package cn.cyc.ai.cog.runtime.session.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 会话消息实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_conversation_message")
public class ConversationMessageEntity extends BaseEntity {

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 消息 ID。
     */
    private String messageId;

    /**
     * 会话 ID。
     */
    private String sessionId;

    /**
     * 消息角色。
     */
    private String role;

    /**
     * 消息内容。
     */
    private String content;

    /**
     * 关联 TraceId。
     */
    private String traceId;

    /**
     * 记录时间。
     */
    private Instant recordedAt;
}
