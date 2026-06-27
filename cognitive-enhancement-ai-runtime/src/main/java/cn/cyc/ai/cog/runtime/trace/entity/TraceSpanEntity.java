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
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_trace_span")
public class TraceSpanEntity extends BaseEntity {

    private Long tenantId;
    private String traceId;
    private String spanId;
    private String parentSpanId;
    private String spanType;
    private String spanName;
    private String status;
    private Long latencyMs;
    private String attributesJson;
    private String errorStack;
    private Instant recordedAt;
}
