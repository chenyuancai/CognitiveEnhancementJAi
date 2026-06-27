package cn.cyc.ai.cog.center.model.catalog.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型主数据实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_model")
public class ModelEntity extends BaseEntity {

    private Long tenantId;
    private String modelCode;
    private String modelName;
    private String modelType;
    private Integer timeoutMs;
    private Integer retryTimes;
    private String status;
    private String fallbackModelCode;
}
