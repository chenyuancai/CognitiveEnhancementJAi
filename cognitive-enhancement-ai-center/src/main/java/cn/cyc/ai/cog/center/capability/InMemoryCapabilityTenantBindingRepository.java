package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBinding;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBindingRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Capability 租户启停绑定内存仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryCapabilityTenantBindingRepository implements CapabilityTenantBindingRepository {

    private final ConcurrentMap<String, CapabilityTenantBinding> storage = new ConcurrentHashMap<>();

    /**
     * 查找人租户And能力。
     *
     * @param tenantCode 租户编码
     * @param capabilityCode 能力编码
     * @return 查找结果
     */
    @Override
    public Optional<CapabilityTenantBinding> findByTenantAndCapability(String tenantCode, String capabilityCode) {
        return Optional.ofNullable(storage.get(key(tenantCode, capabilityCode)));
    }

    /**
     * 执行save。
     *
     * @param binding binding
     * @return 执行结果
     */
    @Override
    public CapabilityTenantBinding save(CapabilityTenantBinding binding) {
        storage.put(key(binding.tenantCode(), binding.capabilityCode()), binding);
        return binding;
    }

    /**
     * 执行键。
     *
     * @param tenantCode 租户编码
     * @param capabilityCode 能力编码
     * @return 执行结果
     */
    private String key(String tenantCode, String capabilityCode) {
        return TenantContext.normalize(tenantCode) + ":" + capabilityCode;
    }
}
