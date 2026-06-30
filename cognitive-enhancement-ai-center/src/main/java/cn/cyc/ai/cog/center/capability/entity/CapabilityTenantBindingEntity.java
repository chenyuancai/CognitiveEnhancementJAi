package cn.cyc.ai.cog.center.capability.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Capability 租户启停绑定实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_capability_tenant_binding")
public class CapabilityTenantBindingEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 能力编码。 */
    private String capabilityCode;
    /** 是否启用。 */
    private Integer enabled;
}
