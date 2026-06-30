package cn.cyc.ai.cog.runtime.trace.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * TraceSpan 实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_trace_span")
public class TraceSpanEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 链路 Trace ID */
    private String traceId;
    /** spanID */
    private String spanId;
    /** parentSpanID */
    private String parentSpanId;
    /** span类型。 */
    private String spanType;
    /** span名称。 */
    private String spanName;
    /** 状态。 */
    private String status;
    /** latencyMs。 */
    private Long latencyMs;
    /** attributesJSON。 */
    private String attributesJson;
    /** 错误Stack。 */
    private String errorStack;
    /** recordedAt。 */
    private Instant recordedAt;
}
