package cn.cyc.ai.cog.center.config;

import cn.cyc.ai.cog.center.support.InMemoryMetadataRepository;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import org.springframework.context.annotation.Bean;

/**
 * 预留的 Center 元数据仓储装配示例，当前默认复用各资源包内已有的内存仓储 Bean。
 *
 * @author cyc
 */
public class CenterMetadataConfiguration {

    /**
     * 装配模型定义仓储。
     *
     * @return 模型定义仓储
     */
    @Bean
    ModelDefinitionRepository modelDefinitionRepository() {
        return new InMemoryModelDefinitionRepository();
    }

    /**
     * 装配提示词模板仓储。
     *
     * @return 提示词模板仓储
     */
    @Bean
    PromptTemplateRepository promptTemplateRepository() {
        return new InMemoryPromptTemplateRepository();
    }

    /**
     * 装配能力定义仓储。
     *
     * @return 能力定义仓储
     */
    @Bean
    CapabilityDefinitionRepository capabilityDefinitionRepository() {
        return new InMemoryCapabilityDefinitionRepository();
    }

    /**
     * 装配 Agent 定义仓储。
     *
     * @return Agent 定义仓储
     */
    @Bean
    AgentDefinitionRepository agentDefinitionRepository() {
        return new InMemoryAgentDefinitionRepository();
    }

    /**
     * 装配技能定义仓储。
     *
     * @return 技能定义仓储
     */
    @Bean
    SkillDefinitionRepository skillDefinitionRepository() {
        return new InMemorySkillDefinitionRepository();
    }

    /**
     * 装配工具定义仓储。
     *
     * @return 工具定义仓储
     */
    @Bean
    ToolDefinitionRepository toolDefinitionRepository() {
        return new InMemoryToolDefinitionRepository();
    }

    private static final class InMemoryModelDefinitionRepository
            extends InMemoryMetadataRepository<ModelDefinition>
            implements ModelDefinitionRepository {
    }

    private static final class InMemoryPromptTemplateRepository
            extends InMemoryMetadataRepository<PromptTemplate>
            implements PromptTemplateRepository {
    }

    private static final class InMemoryCapabilityDefinitionRepository
            extends InMemoryMetadataRepository<CapabilityDefinition>
            implements CapabilityDefinitionRepository {
    }

    private static final class InMemoryAgentDefinitionRepository
            extends InMemoryMetadataRepository<AgentDefinition>
            implements AgentDefinitionRepository {
    }

    private static final class InMemorySkillDefinitionRepository
            extends InMemoryMetadataRepository<SkillDefinition>
            implements SkillDefinitionRepository {
    }

    private static final class InMemoryToolDefinitionRepository
            extends InMemoryMetadataRepository<ToolDefinition>
            implements ToolDefinitionRepository {
    }
}
