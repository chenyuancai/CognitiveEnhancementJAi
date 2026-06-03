package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.center.support.AbstractJsonFileMetadataRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;

/**
 * Tool 定义文件持久化仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentToolDefinitionRepository extends AbstractJsonFileMetadataRepository<ToolDefinition>
        implements ToolDefinitionRepository {

    @Autowired
    public PersistentToolDefinitionRepository(@Value("${cog.persistence.dir:data/cognitive-enhancement-ai}") String persistenceDir) {
        this(Path.of(persistenceDir));
    }

    public PersistentToolDefinitionRepository(Path persistenceDir) {
        super(persistenceDir.resolve("center-tool-definitions.json"), ToolDefinition.class);
    }
}
