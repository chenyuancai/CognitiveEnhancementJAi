package cn.cyc.ai.cog.center.model.catalog.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型与提供商绑定实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_model_provider_binding")
public class ModelProviderBindingEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 模型编码。 */
    private String modelCode;
    /** 提供者编码。 */
    private String providerCode;
    /** endpoint。 */
    private String endpoint;
    /** credentialRef。 */
    private String credentialRef;
    /** api键。 */
    private String apiKey;
    /** routePriority。 */
    private Integer routePriority;
    /** 状态。 */
    private String status;
}
