package cn.cyc.ai.cog.runtime.observation.service;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.observation.repository.InMemoryExecutionRecordRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 默认执行记录器测试。
 *
 * @author cyc
 */
class LoggingExecutionRecorderTest {

    @Test
    void record_shouldMarkSuccessWithoutFailureReason() {
        InMemoryExecutionRecordRepository repository = new InMemoryExecutionRecordRepository();
        LoggingExecutionRecorder recorder = new LoggingExecutionRecorder(repository);

        CapabilityDefinition capability = capability("capability.qa.answer", "agent.qa");
        AgentDefinition agent = agent("agent.qa", "gpt-4o-mini");
        ExecutionContext context = new ExecutionContext("trace-success", null, capability, agent, null, null, null);
        ExecutionResult result = new ExecutionResult("LLM_GENERATED", "已完成", List.of(), Map.of());

        ExecutionRecord record = recorder.record(context, result);

        assertEquals("trace-success", record.traceId());
        assertEquals("LLM_GENERATED", record.resultStatus());
        assertTrue(record.success());
        assertNull(record.failureReason());
        assertNotNull(record.routing());
        assertEquals("agent.qa", record.routing().agentCode());
        assertNotNull(record.result());
        assertEquals("LLM_GENERATED", record.result().status());
        assertEquals(1, repository.listAll().size());
    }

    @Test
    void recordFailure_shouldMarkFailureWithReason() {
        InMemoryExecutionRecordRepository repository = new InMemoryExecutionRecordRepository();
        LoggingExecutionRecorder recorder = new LoggingExecutionRecorder(repository);

        CapabilityDefinition capability = capability("capability.qa.answer", "agent.qa");
        ExecutionContext context = new ExecutionContext("trace-failure", null, capability, null, null, null, null);

        ExecutionRecord record = recorder.recordFailure(context, "模型调用超时");

        assertEquals("trace-failure", record.traceId());
        assertEquals("capability.qa.answer", record.capabilityCode());
        assertNull(record.agentCode());
        assertEquals("FAILED", record.resultStatus());
        assertFalse(record.success());
        assertEquals("模型调用超时", record.failureReason());
        assertEquals(1, repository.listAll().size());
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
