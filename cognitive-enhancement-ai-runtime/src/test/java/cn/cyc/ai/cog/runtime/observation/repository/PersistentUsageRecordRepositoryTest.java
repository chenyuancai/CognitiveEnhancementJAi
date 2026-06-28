package cn.cyc.ai.cog.runtime.observation.repository;

import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.observation.entity.UsageRecordEntity;
import cn.cyc.ai.cog.runtime.observation.mapper.UsageRecordMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 用量记录持久化仓储测试。
 *
 * @author cyc
 */
class PersistentUsageRecordRepositoryTest {

    @Test
    void save_shouldMapDomainToEntity() {
        UsageRecordMapper mapper = mock(UsageRecordMapper.class);
        PersistentUsageRecordRepository repository = new PersistentUsageRecordRepository(mapper);
        Instant recordedAt = Instant.parse("2026-06-09T00:00:00Z");
        UsageRecord record = new UsageRecord(
                "trace-1", "capability.qa.answer", "agent.qa",
                "LLM", "gpt-4o-mini", null,
                12, 34, 46, new BigDecimal("0.001200"), recordedAt);

        repository.save(record);

        ArgumentCaptor<UsageRecordEntity> captor = ArgumentCaptor.forClass(UsageRecordEntity.class);
        verify(mapper).insert(captor.capture());
        UsageRecordEntity entity = captor.getValue();
        assertEquals("trace-1", entity.getTraceId());
        assertEquals("LLM", entity.getExecutorType());
        assertEquals("gpt-4o-mini", entity.getModelCode());
        assertEquals(12, entity.getInputTokenCount());
        assertEquals(34, entity.getOutputTokenCount());
        assertEquals(46, entity.getTotalTokenCount());
        assertEquals(new BigDecimal("0.001200"), entity.getEstimatedCostAmount());
        assertEquals(recordedAt, entity.getRecordedAt());
    }

    @Test
    void listAll_shouldMapEntityToDomain() {
        UsageRecordMapper mapper = mock(UsageRecordMapper.class);
        PersistentUsageRecordRepository repository = new PersistentUsageRecordRepository(mapper);
        UsageRecordEntity entity = new UsageRecordEntity();
        entity.setTraceId("trace-2");
        entity.setCapabilityCode("capability.qa.answer");
        entity.setAgentCode("agent.qa");
        entity.setExecutorType("TOOL");
        entity.setModelCode(null);
        entity.setToolCode("tool.search");
        entity.setInputTokenCount(null);
        entity.setOutputTokenCount(null);
        entity.setTotalTokenCount(null);
        entity.setEstimatedCostAmount(new BigDecimal("0.005000"));
        entity.setRecordedAt(Instant.parse("2026-06-09T01:00:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(entity));

        List<UsageRecord> records = repository.listAll();

        assertEquals(1, records.size());
        UsageRecord record = records.get(0);
        assertEquals("trace-2", record.traceId());
        assertEquals("tool.search", record.toolCode());
        assertEquals(0, record.inputTokenCount());
        assertEquals(0, record.totalTokenCount());
        assertEquals(new BigDecimal("0.005000"), record.estimatedCostAmount());
    }
}
