package cn.cyc.ai.cog.runtime.observation.service;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.observation.repository.InMemoryUsageRecordRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 默认用量记录器测试。
 *
 * @author cyc
 */
class LoggingUsageMeterTest {

    @Test
    void record_shouldPersistLlmTokenUsage() {
        InMemoryUsageRecordRepository repository = new InMemoryUsageRecordRepository();
        LoggingUsageMeter usageMeter = new LoggingUsageMeter(repository);
        CapabilityDefinition capability = capability("capability.chat.generate.bailian", "agent.chat.bailian");
        AgentDefinition agent = agent("agent.chat.bailian", "qwen-plus");
        ExecutionContext context = new ExecutionContext(
                "trace-usage-001",
                new CapabilityExecuteRequest("capability.chat.generate.bailian", Map.of("question", "你好"), Map.of()),
                capability,
                agent,
                null,
                List.of(),
                Map.of()
        );
        LlmInvocationResult llmResult = new LlmInvocationResult(
                "LLM", "bailian", "qwen-plus", null, "你好", "你好，我是助手",
                Map.of(), 15, 9, 24, 321L, false);
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("executorType", "LLM");
        output.put("invocationResult", llmResult);
        output.put("llmResult", llmResult);
        ExecutionResult executionResult = new ExecutionResult(
                "LLM_GENERATED", "ok", List.of(), output);

        UsageRecord record = usageMeter.record(context, executionResult);

        assertEquals("trace-usage-001", record.traceId());
        assertEquals("LLM", record.executorType());
        assertEquals("qwen-plus", record.modelCode());
        assertEquals(15, record.inputTokenCount());
        assertEquals(9, record.outputTokenCount());
        assertEquals(24, record.totalTokenCount());
        assertEquals(0, new BigDecimal("0.000240").compareTo(record.estimatedCostAmount()));
        assertEquals(1, repository.listAll().size());
    }

    @Test
    void record_shouldPersistLlmTokensWhenToolThenLlmOutputContainsLlmResult() {
        InMemoryUsageRecordRepository repository = new InMemoryUsageRecordRepository();
        LoggingUsageMeter usageMeter = new LoggingUsageMeter(repository);
        CapabilityDefinition capability = capability("capability.qa.answer", "agent.qa");
        AgentDefinition agent = agent("agent.qa", "gpt-4o-mini");
        ExecutionContext context = new ExecutionContext(
                "trace-usage-002",
                new CapabilityExecuteRequest("capability.qa.answer", Map.of("question", "hello"), Map.of()),
                capability,
                agent,
                null,
                List.of(),
                Map.of()
        );
        LlmInvocationResult llmResult = new LlmInvocationResult(
                "LLM", "openai", "gpt-4o-mini", "prompt.qa.default", "prompt", "answer",
                Map.of(), 20, 10, 30, 120L, true);
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("executorType", "TOOL_THEN_LLM");
        output.put("toolResult", Map.of("toolCode", "tool.search"));
        output.put("llmResult", llmResult);
        ExecutionResult executionResult = new ExecutionResult(
                "TOOL_THEN_LLM", "ok", List.of(), output);

        UsageRecord record = usageMeter.record(context, executionResult);

        assertEquals(20, record.inputTokenCount());
        assertEquals(10, record.outputTokenCount());
        assertEquals(30, record.totalTokenCount());
        assertEquals("gpt-4o-mini", record.modelCode());
        assertEquals(0, new BigDecimal("0.000300").compareTo(record.estimatedCostAmount()));
    }

    @Test
    void record_shouldPersistReactModelAndTokenUsage() {
        InMemoryUsageRecordRepository repository = new InMemoryUsageRecordRepository();
        LoggingUsageMeter usageMeter = new LoggingUsageMeter(repository);
        CapabilityDefinition capability = capability("capability.qa.answer", "agent.qa");
        AgentDefinition agent = agent("agent.qa", "gpt-4o-mini");
        ExecutionContext context = new ExecutionContext(
                "trace-usage-react-001",
                new CapabilityExecuteRequest("capability.qa.answer", Map.of("question", "hello"), Map.of()),
                capability,
                agent,
                null,
                List.of(),
                Map.of()
        );
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("executorType", "REACT");
        output.put("modelCode", "gpt-4o-mini");
        output.put("totalTokens", 42);
        ExecutionResult executionResult = new ExecutionResult(
                "SUCCESS", "answer", List.of(), output);

        UsageRecord record = usageMeter.record(context, executionResult);

        assertEquals("REACT", record.executorType());
        assertEquals("gpt-4o-mini", record.modelCode());
        assertEquals(42, record.totalTokenCount());
        assertEquals(0, new BigDecimal("0.000420").compareTo(record.estimatedCostAmount()));
    }

    private CapabilityDefinition capability(String capabilityCode, String agentCode) {
        SchemaDefinition schema = new SchemaDefinition("object", "schema", true, Map.of(), null, List.of());
        return new CapabilityDefinition(
                capabilityCode, "问答", "desc", schema, schema, Map.of(),
                ExecutionMode.SYNC, agentCode, RiskLevel.LOW, false, CommonStatus.ENABLED
        );
    }

    private AgentDefinition agent(String agentCode, String modelCode) {
        return new AgentDefinition(
                agentCode, "问答 Agent", "role", "goal", modelCode,
                4, BigDecimal.ONE, 20000, List.of("skill.qa"), Map.of(), CommonStatus.ENABLED
        );
    }
}
