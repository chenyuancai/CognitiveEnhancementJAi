package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.center.support.AbstractJsonFileMetadataRepository;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;

/**
 * 模型定义文件持久化仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentModelDefinitionRepository extends AbstractJsonFileMetadataRepository<ModelDefinition>
        implements ModelDefinitionRepository {

    @Autowired
    public PersistentModelDefinitionRepository(@Value("${cog.persistence.dir:data/cognitive-enhancement-ai}") String persistenceDir) {
        this(Path.of(persistenceDir));
    }

    public PersistentModelDefinitionRepository(Path persistenceDir) {
        super(persistenceDir.resolve("center-model-definitions.json"), ModelDefinition.class);
    }
}
