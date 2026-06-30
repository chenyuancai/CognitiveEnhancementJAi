package cn.cyc.ai.cog.runtime.harness.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * HarnessStepReport实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "qz_rt_harness_step_report", autoResultMap = true)
public class HarnessStepReportEntity extends BaseEntity {
    /** harnessID */
    private String harnessId;
    /** sequence。 */
    private Integer sequence;
    /** step编码。 */
    private String stepCode;
    /** step名称。 */
    private String stepName;
    /** 状态。 */
    private String status;
    /** durationMs。 */
    private Long durationMs;
    /** 消息。 */
    private String message;
    /** detailsJSON。 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String detailsJson;
}
