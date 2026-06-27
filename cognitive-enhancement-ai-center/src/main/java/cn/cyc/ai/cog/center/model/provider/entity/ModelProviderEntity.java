package cn.cyc.ai.cog.center.model.provider.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型提供商实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_model_provider")
public class ModelProviderEntity extends BaseEntity {

    private Long tenantId;
    private String providerCode;
    private String providerName;
    private String providerType;
    private String defaultEndpoint;
    private String defaultCredentialRef;
    private String apiKey;
    private String description;
    private String status;
}
