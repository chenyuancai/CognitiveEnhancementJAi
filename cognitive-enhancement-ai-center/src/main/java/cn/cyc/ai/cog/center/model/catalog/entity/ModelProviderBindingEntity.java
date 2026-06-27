package cn.cyc.ai.cog.center.model.catalog.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型与提供商绑定实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_model_provider_binding")
public class ModelProviderBindingEntity extends BaseEntity {

    private Long tenantId;
    private String modelCode;
    private String providerCode;
    private String endpoint;
    private String credentialRef;
    private String apiKey;
    private Integer routePriority;
    private String status;
}
