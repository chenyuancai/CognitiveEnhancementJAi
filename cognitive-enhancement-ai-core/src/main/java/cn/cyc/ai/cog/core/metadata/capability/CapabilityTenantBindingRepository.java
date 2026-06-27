package cn.cyc.ai.cog.core.metadata.capability;

import java.util.Optional;

/**
 * Capability 租户启停绑定仓储接口。
 *
 * @author cyc
 */
public interface CapabilityTenantBindingRepository {

    /**
     * 按租户与能力编码查询绑定关系。
     *
     * @param tenantCode     租户编码
     * @param capabilityCode 能力编码
     * @return 绑定关系
     */
    Optional<CapabilityTenantBinding> findByTenantAndCapability(String tenantCode, String capabilityCode);

    /**
     * 保存绑定关系。
     *
     * @param binding 绑定关系
     * @return 保存后的绑定
     */
    CapabilityTenantBinding save(CapabilityTenantBinding binding);
}
