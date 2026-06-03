package cn.cyc.ai.cog.runtime.harness.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "harness_step_report", autoResultMap = true)
public class HarnessStepReportEntity extends BaseEntity {
    private String harnessId;
    private Integer sequence;
    private String stepCode;
    private String stepName;
    private String status;
    private Long durationMs;
    private String message;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String detailsJson;
}
