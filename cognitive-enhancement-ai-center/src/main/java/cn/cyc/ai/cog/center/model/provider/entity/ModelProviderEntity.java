package cn.cyc.ai.cog.center.model.provider.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型提供商实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_model_provider")
public class ModelProviderEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 提供者编码。 */
    private String providerCode;
    /** 提供者名称。 */
    private String providerName;
    /** 提供者类型。 */
    private String providerType;
    /** 默认Endpoint。 */
    private String defaultEndpoint;
    /** 默认CredentialRef。 */
    private String defaultCredentialRef;
    /** api键。 */
    private String apiKey;
    /** 描述。 */
    private String description;
    /** 状态。 */
    private String status;
}
