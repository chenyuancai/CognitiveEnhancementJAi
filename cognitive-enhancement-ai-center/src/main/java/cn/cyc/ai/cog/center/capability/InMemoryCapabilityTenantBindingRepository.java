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
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryCapabilityTenantBindingRepository implements CapabilityTenantBindingRepository {

    private final ConcurrentMap<String, CapabilityTenantBinding> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<CapabilityTenantBinding> findByTenantAndCapability(String tenantCode, String capabilityCode) {
        return Optional.ofNullable(storage.get(key(tenantCode, capabilityCode)));
    }

    @Override
    public CapabilityTenantBinding save(CapabilityTenantBinding binding) {
        storage.put(key(binding.tenantCode(), binding.capabilityCode()), binding);
        return binding;
    }

    private String key(String tenantCode, String capabilityCode) {
        return TenantContext.normalize(tenantCode) + ":" + capabilityCode;
    }
}
