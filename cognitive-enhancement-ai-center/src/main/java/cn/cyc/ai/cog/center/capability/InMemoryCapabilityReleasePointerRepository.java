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
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryCapabilityReleasePointerRepository implements CapabilityReleasePointerRepository {

    private final ConcurrentMap<String, CapabilityReleasePointer> storage = new ConcurrentHashMap<>();

    /**
     * 查找人能力编码。
     *
     * @param capabilityCode 能力编码
     * @return 查找结果
     */
    @Override
    public Optional<CapabilityReleasePointer> findByCapabilityCode(String capabilityCode) {
        return Optional.ofNullable(storage.get(key(capabilityCode)));
    }

    /**
     * 执行save。
     *
     * @param pointer pointer
     * @return 执行结果
     */
    @Override
    public CapabilityReleasePointer save(CapabilityReleasePointer pointer) {
        storage.put(key(pointer.capabilityCode()), pointer);
        return pointer;
    }

    /**
     * 执行键。
     *
     * @param capabilityCode 能力编码
     * @return 执行结果
     */
    private String key(String capabilityCode) {
        return TenantContext.currentTenantCode() + ":" + capabilityCode;
    }
}
