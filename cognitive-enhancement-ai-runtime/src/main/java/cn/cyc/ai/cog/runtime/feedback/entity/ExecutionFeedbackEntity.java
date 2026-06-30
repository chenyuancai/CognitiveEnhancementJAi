package cn.cyc.ai.cog.runtime.feedback.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 执行反馈实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_execution_feedback")
public class ExecutionFeedbackEntity extends BaseEntity {

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 反馈 ID。
     */
    private String feedbackId;

    /**
     * 链路 TraceId。
     */
    private String traceId;

    /**
     * 会话 ID。
     */
    private String sessionId;

    /**
     * 评分 1-5。
     */
    private Integer rating;

    /**
     * AI 原始回答。
     */
    private String originalAnswer;

    /**
     * 用户修正回答。
     */
    private String correctedAnswer;

    /**
     * 反馈备注。
     */
    private String commentText;

    /**
     * 记录时间。
     */
    private Instant recordedAt;
}
