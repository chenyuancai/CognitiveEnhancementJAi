package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.center.support.AbstractInMemoryMetadataRepository;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

/**
 * Skill 定义内存仓储实现，负责为主链路提供轻量级技能定义存储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemorySkillDefinitionRepository extends AbstractInMemoryMetadataRepository<SkillDefinition>
        implements SkillDefinitionRepository {
}
