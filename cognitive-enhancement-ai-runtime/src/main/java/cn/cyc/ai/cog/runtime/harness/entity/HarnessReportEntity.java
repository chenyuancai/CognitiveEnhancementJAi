package cn.cyc.ai.cog.runtime.harness.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "harness_report", autoResultMap = true)
public class HarnessReportEntity extends BaseEntity {
    private String harnessId;
    private String traceId;
    private String status;
    private Instant startTime;
    private Instant endTime;
    private Long totalDurationMs;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String scenarioJson;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String summaryJson;
}
