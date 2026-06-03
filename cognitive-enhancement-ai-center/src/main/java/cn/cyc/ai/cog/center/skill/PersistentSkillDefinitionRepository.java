package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.center.support.AbstractJsonFileMetadataRepository;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;

/**
 * Skill 定义文件持久化仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentSkillDefinitionRepository extends AbstractJsonFileMetadataRepository<SkillDefinition>
        implements SkillDefinitionRepository {

    @Autowired
    public PersistentSkillDefinitionRepository(@Value("${cog.persistence.dir:data/cognitive-enhancement-ai}") String persistenceDir) {
        this(Path.of(persistenceDir));
    }

    public PersistentSkillDefinitionRepository(Path persistenceDir) {
        super(persistenceDir.resolve("center-skill-definitions.json"), SkillDefinition.class);
    }
}
