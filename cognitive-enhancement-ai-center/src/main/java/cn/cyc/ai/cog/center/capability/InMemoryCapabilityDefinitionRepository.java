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
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryCapabilityDefinitionRepository implements CapabilityDefinitionRepository {

    private final ConcurrentMap<String, CapabilityDefinition> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<CapabilityDefinition> findByCode(String code) {
        return findPublishedByCapabilityCode(code);
    }

    @Override
    public List<CapabilityDefinition> listAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(CapabilityDefinition::capabilityCode)
                        .thenComparing(CapabilityDefinition::version))
                .toList();
    }

    @Override
    public List<CapabilityDefinition> listVersionsByCapabilityCode(String capabilityCode) {
        return storage.values().stream()
                .filter(item -> item.capabilityCode().equals(capabilityCode))
                .sorted(Comparator.comparing(CapabilityDefinition::version))
                .toList();
    }

    @Override
    public CapabilityDefinition save(CapabilityDefinition definition) {
        storage.put(versionKey(definition.capabilityCode(), definition.version()), definition);
        return definition;
    }

    private String versionKey(String capabilityCode, String version) {
        return capabilityCode + "@" + version;
    }
}
