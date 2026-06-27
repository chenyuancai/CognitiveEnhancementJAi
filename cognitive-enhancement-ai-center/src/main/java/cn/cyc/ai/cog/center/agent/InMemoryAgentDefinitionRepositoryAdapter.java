package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.center.support.AbstractInMemoryMetadataRepository;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

/**
 * Agent 定义内存仓储实现，负责为主链路提供轻量级 Agent 定义存储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryAgentDefinitionRepositoryAdapter extends AbstractInMemoryMetadataRepository<AgentDefinition>
        implements AgentDefinitionRepository {
}
