package cn.cyc.ai.cog.runtime.session.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 会话实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_conversation_session")
public class ConversationSessionEntity extends BaseEntity {

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 会话 ID。
     */
    private String sessionId;

    /**
     * 用户 ID。
     */
    private String userId;

    /**
     * 能力编码。
     */
    private String capabilityCode;

    /**
     * 会话标题。
     */
    private String title;

    /**
     * 会话状态。
     */
    private String status;

    /**
     * 创建时间。
     */
    private Instant createdAt;

    /**
     * 更新时间。
     */
    private Instant updatedAt;
}
