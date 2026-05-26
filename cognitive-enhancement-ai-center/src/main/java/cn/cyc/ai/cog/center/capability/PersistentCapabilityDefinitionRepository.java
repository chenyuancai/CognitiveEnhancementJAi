package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.support.AbstractJsonFileMetadataRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;

/**
 * 能力定义文件持久化仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentCapabilityDefinitionRepository extends AbstractJsonFileMetadataRepository<CapabilityDefinition>
        implements CapabilityDefinitionRepository {

    public PersistentCapabilityDefinitionRepository(@Value("${cog.persistence.dir:data/cognitive-enhancement-ai}") String persistenceDir) {
        this(Path.of(persistenceDir));
    }

    public PersistentCapabilityDefinitionRepository(Path persistenceDir) {
        super(persistenceDir.resolve("center-capability-definitions.json"), CapabilityDefinition.class);
    }
}
