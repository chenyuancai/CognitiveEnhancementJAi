package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.center.support.AbstractJsonFileMetadataRepository;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;

/**
 * Agent 定义文件持久化仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentAgentDefinitionRepository extends AbstractJsonFileMetadataRepository<AgentDefinition>
        implements AgentDefinitionRepository {

    public PersistentAgentDefinitionRepository(@Value("${cog.persistence.dir:data/cognitive-enhancement-ai}") String persistenceDir) {
        this(Path.of(persistenceDir));
    }

    public PersistentAgentDefinitionRepository(Path persistenceDir) {
        super(persistenceDir.resolve("center-agent-definitions.json"), AgentDefinition.class);
    }
}
