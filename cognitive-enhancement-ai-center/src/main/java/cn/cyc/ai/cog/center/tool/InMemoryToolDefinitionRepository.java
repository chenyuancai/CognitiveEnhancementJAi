package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.center.support.AbstractInMemoryMetadataRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

/**
 * Tool 定义内存仓储实现，负责为主链路提供轻量级工具定义存储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryToolDefinitionRepository extends AbstractInMemoryMetadataRepository<ToolDefinition>
        implements ToolDefinitionRepository {
}
