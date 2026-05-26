package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.support.AbstractInMemoryMetadataRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

/**
 * 能力定义内存仓储实现，负责为主链路提供轻量级能力定义存储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryCapabilityDefinitionRepository extends AbstractInMemoryMetadataRepository<CapabilityDefinition>
        implements CapabilityDefinitionRepository {
}
