package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 能力定义内存仓储实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryCapabilityDefinitionRepository implements CapabilityDefinitionRepository {

    private final ConcurrentMap<String, CapabilityDefinition> storage = new ConcurrentHashMap<>();

    /**
     * 查找人编码。
     *
     * @param code 编码
     * @return 查找结果
     */
    @Override
    public Optional<CapabilityDefinition> findByCode(String code) {
        return findPublishedByCapabilityCode(code);
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<CapabilityDefinition> listAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(CapabilityDefinition::capabilityCode)
                        .thenComparing(CapabilityDefinition::version))
                .toList();
    }

    /**
     * 查询Versions人能力编码列表。
     *
     * @param capabilityCode 能力编码
     * @return 结果列表
     */
    @Override
    public List<CapabilityDefinition> listVersionsByCapabilityCode(String capabilityCode) {
        return storage.values().stream()
                .filter(item -> item.capabilityCode().equals(capabilityCode))
                .sorted(Comparator.comparing(CapabilityDefinition::version))
                .toList();
    }

    /**
     * 执行save。
     *
     * @param definition definition
     * @return 执行结果
     */
    @Override
    public CapabilityDefinition save(CapabilityDefinition definition) {
        storage.put(versionKey(definition.capabilityCode(), definition.version()), definition);
        return definition;
    }

    /**
     * 执行版本号键。
     *
     * @param capabilityCode 能力编码
     * @param version 版本号
     * @return 执行结果
     */
    private String versionKey(String capabilityCode, String version) {
        return capabilityCode + "@" + version;
    }
}
