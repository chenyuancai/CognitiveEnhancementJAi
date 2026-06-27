package cn.cyc.ai.cog.center.capability.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Capability 租户启停绑定实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_capability_tenant_binding")
public class CapabilityTenantBindingEntity extends BaseEntity {

    private Long tenantId;
    private String capabilityCode;
    private Integer enabled;
}
