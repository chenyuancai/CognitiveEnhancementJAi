package cn.cyc.ai.cog.core.metadata.capability;

import cn.cyc.ai.cog.core.metadata.MetadataRepository;

import java.util.List;
import java.util.Optional;

/**
 * 能力定义仓储接口。
 */
public interface CapabilityDefinitionRepository extends MetadataRepository<CapabilityDefinition> {

    /**
     * 按编码与版本查询能力定义（灰度路由可加载 OFFLINE 基线）。
     *
     * @param capabilityCode 能力编码
     * @param version        版本号
     * @return 能力定义
     */
    default Optional<CapabilityDefinition> findByCapabilityCodeAndVersion(String capabilityCode, String version) {
        return listVersionsByCapabilityCode(capabilityCode).stream()
                .filter(item -> item.version().equals(version))
                .findFirst();
    }

    /**
     * 列出指定能力编码的全部版本。
     *
     * @param capabilityCode 能力编码
     * @return 版本列表
     */
    List<CapabilityDefinition> listVersionsByCapabilityCode(String capabilityCode);

    /**
     * 查询当前已发布版本（同 capabilityCode 至多一条 PUBLISHED）。
     *
     * @param capabilityCode 能力编码
     * @return 已发布版本
     */
    default Optional<CapabilityDefinition> findPublishedByCapabilityCode(String capabilityCode) {
        return listVersionsByCapabilityCode(capabilityCode).stream()
                .filter(item -> item.lifecycleStatus() == CapabilityLifecycleStatus.PUBLISHED)
                .findFirst();
    }
}
