package cn.cyc.ai.cog.center.support;

import cn.cyc.ai.cog.center.agent.PersistentAgentDefinitionRepository;
import cn.cyc.ai.cog.center.capability.PersistentCapabilityDefinitionRepository;
import cn.cyc.ai.cog.center.model.PersistentModelDefinitionRepository;
import cn.cyc.ai.cog.center.prompt.PersistentPromptTemplateRepository;
import cn.cyc.ai.cog.center.skill.PersistentSkillDefinitionRepository;
import cn.cyc.ai.cog.center.tool.PersistentToolDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PersistentMetadataRepositoryTest {

    @TempDir
    private Path tempDir;

    @Test
    void shouldRestoreAllMetadataDefinitionsFromPersistentFiles() {
        new PersistentModelDefinitionRepository(tempDir).save(modelDefinition());
        new PersistentPromptTemplateRepository(tempDir).save(promptTemplate());
        new PersistentCapabilityDefinitionRepository(tempDir).save(capabilityDefinition());
        new PersistentAgentDefinitionRepository(tempDir).save(agentDefinition());
        new PersistentSkillDefinitionRepository(tempDir).save(skillDefinition());
        new PersistentToolDefinitionRepository(tempDir).save(toolDefinition());

        assertThat(new PersistentModelDefinitionRepository(tempDir).findByCode("gpt-4o-mini"))
                .contains(modelDefinition());
        assertThat(new PersistentPromptTemplateRepository(tempDir).findByCode("prompt.qa.default"))
                .contains(promptTemplate());
        assertThat(new PersistentCapabilityDefinitionRepository(tempDir).findByCode("capability.qa.answer"))
                .contains(capabilityDefinition());
        assertThat(new PersistentAgentDefinitionRepository(tempDir).findByCode("agent.qa"))
                .contains(agentDefinition());
        assertThat(new PersistentSkillDefinitionRepository(tempDir).findByCode("skill.qa"))
                .contains(skillDefinition());
        assertThat(new PersistentToolDefinitionRepository(tempDir).findByCode("tool.search"))
                .contains(toolDefinition());
    }

    private static ModelDefinition modelDefinition() {
        return new ModelDefinition(
                "openai",
                "OpenAI",
                "gpt-4o-mini",
                "GPT-4o Mini",
                "CHAT",
                "https://api.openai.com/v1/chat/completions",
                "credential/openai/default",
                30000,
                2,
                CommonStatus.ENABLED,
                10,
                null
        );
    }

    private static PromptTemplate promptTemplate() {
        return new PromptTemplate(
                "prompt.qa.default",
                "默认问答模板",
                "qa",
                "v1",
                "请结合上下文回答用户问题：{{question}}",
                schema(),
                schema(),
                CommonStatus.ENABLED,
                Instant.parse("2026-05-11T00:00:00Z")
        );
    }

    private static CapabilityDefinition capabilityDefinition() {
        return new CapabilityDefinition(
                "capability.qa.answer",
                "智能问答",
                "对外提供基础问答能力",
                schema(),
                schema(),
                Map.of(),
                ExecutionMode.SYNC,
                "agent.qa",
                RiskLevel.LOW,
                false,
                CommonStatus.ENABLED
        );
    }

    private static AgentDefinition agentDefinition() {
        return new AgentDefinition(
                "agent.qa",
                "问答代理",
                "专业问答助手",
                "为用户输出可靠答案",
                "gpt-4o-mini",
                6,
                new BigDecimal("1.50"),
                20000,
                List.of("skill.qa"),
                Map.of(),
                CommonStatus.ENABLED
        );
    }

    private static SkillDefinition skillDefinition() {
        return new SkillDefinition(
                "skill.qa",
                "问答技能",
                "DOMAIN",
                "优先基于事实回答。",
                List.of("tool.search"),
                RiskLevel.LOW,
                List.of("不得编造来源"),
                List.of("用户询问事实类问题时可先搜索"),
                CommonStatus.ENABLED
        );
    }

    private static ToolDefinition toolDefinition() {
        return new ToolDefinition(
                "tool.search",
                "搜索工具",
                ToolProtocolType.JAVA_LOCAL,
                schema(),
                schema(),
                "search:query",
                5000,
                new cn.cyc.ai.cog.core.metadata.tool.RetryPolicy(1),
                "demoSearchTool",
                CommonStatus.ENABLED
        );
    }

    private static SchemaDefinition schema() {
        return new SchemaDefinition("object", "测试结构", true, Map.of(), null, List.of());
    }
}
