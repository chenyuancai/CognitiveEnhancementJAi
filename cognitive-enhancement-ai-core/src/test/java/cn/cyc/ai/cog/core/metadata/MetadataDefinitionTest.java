package cn.cyc.ai.cog.core.metadata;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionService;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.tool.RetryPolicy;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetadataDefinitionTest {

    private final SchemaDefinition inputSchema = new SchemaDefinition(
            "object",
            "能力输入",
            true,
            Map.of("question", new SchemaDefinition("string", "用户问题", true, Map.of(), null, List.of())),
            null,
            List.of()
    );

    private final SchemaDefinition outputSchema = new SchemaDefinition(
            "object",
            "能力输出",
            true,
            Map.of("answer", new SchemaDefinition("string", "回答内容", true, Map.of(), null, List.of())),
            null,
            List.of()
    );

    @Test
    void shouldConstructCoreMetadataDefinitionsWithKeyFields() {
        ModelDefinition model = new ModelDefinition(
                "openai",
                "OpenAI",
                "gpt-4o-mini",
                "GPT-4o mini",
                "CHAT",
                "https://api.openai.com/v1",
                "openai.default",
                15_000,
                2,
                CommonStatus.ENABLED,
                10,
                "gpt-4.1-mini"
        );
        PromptTemplate prompt = new PromptTemplate(
                "qa-default",
                "问答默认模板",
                "qa",
                "v1",
                "请根据上下文回答问题：{{question}}",
                inputSchema,
                outputSchema,
                CommonStatus.ENABLED,
                Instant.parse("2026-05-11T00:00:00Z")
        );
        CapabilityDefinition capability = new CapabilityDefinition(
                "qa.answer",
                "智能问答",
                "对外提供问答能力",
                inputSchema,
                outputSchema,
                Map.of("temperature", new ParameterConstraintDefinition("number", false, 0D, 2D, false)),
                ExecutionMode.SYNC,
                "agent.qa",
                RiskLevel.LOW,
                false,
                CommonStatus.ENABLED
        );
        AgentDefinition agent = new AgentDefinition(
                "agent.qa",
                "问答代理",
                "专业问答助手",
                "输出准确回答",
                model.modelCode(),
                6,
                new BigDecimal("1.50"),
                20_000,
                List.of("skill.qa"),
                Map.of("temperature", new ParameterConstraintDefinition("number", false, 0D, 1.5D, false)),
                CommonStatus.ENABLED
        );
        SkillDefinition skill = new SkillDefinition(
                "skill.qa",
                "问答技能",
                "DOMAIN",
                "优先基于事实回答，不确定时明确说明。",
                List.of("tool.search"),
                RiskLevel.LOW,
                List.of("不得编造来源"),
                List.of("用户问天气时先调用搜索工具"),
                CommonStatus.ENABLED
        );
        ToolDefinition tool = new ToolDefinition(
                "tool.search",
                "搜索工具",
                ToolProtocolType.HTTP,
                inputSchema,
                outputSchema,
                "search:query",
                5_000,
                new RetryPolicy(2),
                "searchToolAdapter",
                CommonStatus.ENABLED
        );

        assertEquals("gpt-4o-mini", model.modelCode());
        assertEquals("qa-default", prompt.promptCode());
        assertEquals("agent.qa", capability.boundAgentCode());
        assertTrue(capability.parameterConstraints().containsKey("temperature"));
        assertTrue(agent.parameterConstraints().containsKey("temperature"));
        assertEquals(List.of("skill.qa"), agent.allowedSkillCodes());
        assertEquals(List.of("tool.search"), skill.boundToolCodes());
        assertEquals(ToolProtocolType.HTTP, tool.protocolType());
        assertEquals(2, tool.retryPolicy().maxAttempts());
    }

    @Test
    void shouldExposeDefinitionsThroughMinimalRepositoryAndServiceContracts() {
        AgentDefinition agent = new AgentDefinition(
                "agent.route",
                "路由代理",
                "负责分发请求",
                "调用匹配能力",
                "gpt-4o-mini",
                4,
                new BigDecimal("2.00"),
                10_000,
                List.of("skill.route"),
                Map.of(),
                CommonStatus.ENABLED
        );
        InMemoryAgentDefinitionRepository repository = new InMemoryAgentDefinitionRepository(agent);
        AgentDefinitionService service = new DefaultAgentDefinitionService(repository);

        Optional<AgentDefinition> loaded = service.findByCode("agent.route");

        assertTrue(loaded.isPresent());
        assertSame(agent, loaded.orElseThrow());
        assertEquals(List.of(agent), service.listAll());
        assertEquals("agent.route", loaded.orElseThrow().code());
        assertFalse(service.findByCode("missing").isPresent());
    }

    private static final class DefaultAgentDefinitionService implements AgentDefinitionService {

        private final AgentDefinitionRepository repository;

        private DefaultAgentDefinitionService(AgentDefinitionRepository repository) {
            this.repository = repository;
        }

        @Override
        public Optional<AgentDefinition> findByCode(String code) {
            return repository.findByCode(code);
        }

        @Override
        public List<AgentDefinition> listAll() {
            return repository.listAll();
        }

        @Override
        public AgentDefinition save(AgentDefinition definition) {
            return repository.save(definition);
        }
    }

    private static final class InMemoryAgentDefinitionRepository implements AgentDefinitionRepository {

        private AgentDefinition definition;

        private InMemoryAgentDefinitionRepository(AgentDefinition definition) {
            this.definition = definition;
        }

        @Override
        public Optional<AgentDefinition> findByCode(String code) {
            if (definition.code().equals(code)) {
                return Optional.of(definition);
            }
            return Optional.empty();
        }

        @Override
        public List<AgentDefinition> listAll() {
            return List.of(definition);
        }

        @Override
        public AgentDefinition save(AgentDefinition definition) {
            this.definition = definition;
            return definition;
        }
    }
}
