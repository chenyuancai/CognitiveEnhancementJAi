package cn.cyc.ai.cog.core.metadata.capability;

import java.util.Optional;

/**
 * Capability 发布指针仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface CapabilityReleasePointerRepository {

    /**
     * 按能力编码查询发布指针。
     *
     * @param capabilityCode 能力编码
     * @return 发布指针
     */
    Optional<CapabilityReleasePointer> findByCapabilityCode(String capabilityCode);

    /**
     * 保存发布指针。
     *
     * @param pointer 发布指针
     * @return 保存后的指针
     */
    CapabilityReleasePointer save(CapabilityReleasePointer pointer);
}
