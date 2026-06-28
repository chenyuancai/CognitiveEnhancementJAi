package cn.cyc.ai.cog.runtime.observation.repository;

import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.observation.entity.ExecutionRecordEntity;
import cn.cyc.ai.cog.runtime.observation.mapper.ExecutionRecordMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 执行记录持久化仓储测试。
 *
 * @author cyc
 */
class PersistentExecutionRecordRepositoryTest {

    private ObjectMapper objectMapper;
    private ExecutionRecordMapper mapper;
    private PersistentExecutionRecordRepository repository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mapper = mock(ExecutionRecordMapper.class);
        repository = new PersistentExecutionRecordRepository(mapper, objectMapper);
    }

    @Test
    void save_shouldMapDomainToEntity() {
        Instant recordedAt = Instant.parse("2026-06-09T00:00:00Z");
        ExecutionRecord record = new ExecutionRecord(
                "trace-1", "capability.qa.answer", "agent.qa",
                "FAILED", false, "模型调用超时", recordedAt,
                new ExecutionRecord.ExecutionInputDetail(Map.of("question", "hello"), Map.of()),
                new ExecutionRecord.ExecutionRoutingDetail(
                        "capability.qa.answer", "问答能力", "agent.qa", "问答 Agent",
                        "prompt.qa.default", List.of("skill.qa"), "qwen-plus"),
                null
        );

        repository.save(record);

        ArgumentCaptor<ExecutionRecordEntity> captor = ArgumentCaptor.forClass(ExecutionRecordEntity.class);
        verify(mapper).insert(captor.capture());
        ExecutionRecordEntity entity = captor.getValue();
        assertEquals("trace-1", entity.getTraceId());
        assertEquals("capability.qa.answer", entity.getCapabilityCode());
        assertEquals("agent.qa", entity.getAgentCode());
        assertEquals("FAILED", entity.getResultStatus());
        assertFalse(entity.getSuccess());
        assertEquals("模型调用超时", entity.getFailureReason());
        assertEquals(recordedAt, entity.getRecordedAt());
        assertTrue(entity.getInputJson().contains("hello"));
        assertTrue(entity.getRoutingJson().contains("skill.qa"));
    }

    @Test
    void listAll_shouldMapEntityToDomain() {
        ExecutionRecordEntity entity = new ExecutionRecordEntity();
        entity.setTraceId("trace-2");
        entity.setCapabilityCode("capability.qa.answer");
        entity.setAgentCode("agent.qa");
        entity.setResultStatus("LLM_GENERATED");
        entity.setSuccess(true);
        entity.setFailureReason(null);
        entity.setRecordedAt(Instant.parse("2026-06-09T01:00:00Z"));
        entity.setResultJson("""
                {"status":"LLM_GENERATED","message":"已完成","allowedSkillCodes":[],"output":{}}
                """);
        when(mapper.selectList(any())).thenReturn(List.of(entity));

        List<ExecutionRecord> records = repository.listAll();

        assertEquals(1, records.size());
        ExecutionRecord record = records.get(0);
        assertEquals("trace-2", record.traceId());
        assertEquals("LLM_GENERATED", record.resultStatus());
        assertTrue(record.success());
        assertNull(record.failureReason());
        assertEquals("LLM_GENERATED", record.result().status());
    }

    @Test
    void findByTraceId_shouldReturnMappedRecord() {
        ExecutionRecordEntity entity = new ExecutionRecordEntity();
        entity.setTraceId("trace-detail");
        entity.setCapabilityCode("capability.qa.answer");
        entity.setAgentCode("agent.qa");
        entity.setResultStatus("TOOL_INVOKED");
        entity.setSuccess(true);
        entity.setRecordedAt(Instant.parse("2026-06-09T02:00:00Z"));
        entity.setInputJson("{\"params\":{\"question\":\"test\"},\"parameters\":{}}");
        when(mapper.selectOne(any())).thenReturn(entity);

        Optional<ExecutionRecord> found = repository.findByTraceId("trace-detail");

        assertTrue(found.isPresent());
        assertEquals("test", found.get().input().params().get("question"));
    }
}
