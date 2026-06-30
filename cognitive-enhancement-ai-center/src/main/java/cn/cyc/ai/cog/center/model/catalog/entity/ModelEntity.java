package cn.cyc.ai.cog.center.model.catalog.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型主数据实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_model")
public class ModelEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 模型编码。 */
    private String modelCode;
    /** 模型名称。 */
    private String modelName;
    /** 模型类型。 */
    private String modelType;
    /** timeoutMs。 */
    private Integer timeoutMs;
    /** retryTimes。 */
    private Integer retryTimes;
    /** 状态。 */
    private String status;
    /** fallback模型编码。 */
    private String fallbackModelCode;
}
