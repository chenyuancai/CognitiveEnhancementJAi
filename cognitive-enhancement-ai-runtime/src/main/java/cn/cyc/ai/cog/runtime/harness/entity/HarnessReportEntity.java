package cn.cyc.ai.cog.runtime.harness.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * HarnessReport实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "qz_rt_harness_report", autoResultMap = true)
public class HarnessReportEntity extends BaseEntity {
    /** harnessID */
    private String harnessId;
    /** 链路 Trace ID */
    private String traceId;
    /** 状态。 */
    private String status;
    /** start时间。 */
    private Instant startTime;
    /** end时间。 */
    private Instant endTime;
    /** 总数DurationMs。 */
    private Long totalDurationMs;
    /** scenarioJSON。 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String scenarioJson;
    /** 摘要JSON。 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String summaryJson;
}
