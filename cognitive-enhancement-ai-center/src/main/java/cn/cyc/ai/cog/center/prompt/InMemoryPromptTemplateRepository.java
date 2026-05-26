package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.support.AbstractInMemoryMetadataRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

/**
 * Prompt 模板内存仓储实现，负责为主链路提供轻量级模板定义存储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryPromptTemplateRepository extends AbstractInMemoryMetadataRepository<PromptTemplate>
        implements PromptTemplateRepository {
}
