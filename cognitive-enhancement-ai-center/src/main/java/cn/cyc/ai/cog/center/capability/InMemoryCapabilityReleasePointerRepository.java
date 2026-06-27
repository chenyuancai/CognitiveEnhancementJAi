package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointer;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointerRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Capability 发布指针内存仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryCapabilityReleasePointerRepository implements CapabilityReleasePointerRepository {

    private final ConcurrentMap<String, CapabilityReleasePointer> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<CapabilityReleasePointer> findByCapabilityCode(String capabilityCode) {
        return Optional.ofNullable(storage.get(key(capabilityCode)));
    }

    @Override
    public CapabilityReleasePointer save(CapabilityReleasePointer pointer) {
        storage.put(key(pointer.capabilityCode()), pointer);
        return pointer;
    }

    private String key(String capabilityCode) {
        return TenantContext.currentTenantCode() + ":" + capabilityCode;
    }
}
