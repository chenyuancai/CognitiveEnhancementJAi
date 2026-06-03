package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.support.AbstractJsonFileMetadataRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;

/**
 * Prompt 模板文件持久化仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentPromptTemplateRepository extends AbstractJsonFileMetadataRepository<PromptTemplate>
        implements PromptTemplateRepository {

    @Autowired
    public PersistentPromptTemplateRepository(@Value("${cog.persistence.dir:data/cognitive-enhancement-ai}") String persistenceDir) {
        this(Path.of(persistenceDir));
    }

    public PersistentPromptTemplateRepository(Path persistenceDir) {
        super(persistenceDir.resolve("center-prompt-templates.json"), PromptTemplate.class);
    }
}
