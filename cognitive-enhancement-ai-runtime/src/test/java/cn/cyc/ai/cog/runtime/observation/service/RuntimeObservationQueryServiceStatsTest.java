package cn.cyc.ai.cog.runtime.observation.service;

import cn.cyc.ai.cog.runtime.observation.dto.ObservationStatsResult;
import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.observation.repository.InMemoryExecutionRecordRepository;
import cn.cyc.ai.cog.runtime.observation.repository.InMemoryUsageRecordRepository;
import cn.cyc.ai.cog.runtime.observation.spi.ExecutionRecordRepository;
import cn.cyc.ai.cog.runtime.spi.ModelCheckRecordRepository;
import cn.cyc.ai.cog.runtime.observation.spi.UsageRecordRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuntimeObservationQueryServiceStatsTest {

    private InMemoryExecutionRecordRepository executionRepository;
    private InMemoryUsageRecordRepository usageRepository;
    private RuntimeObservationQueryService queryService;

    @BeforeEach
    void setUp() {
        executionRepository = new InMemoryExecutionRecordRepository();
        usageRepository = new InMemoryUsageRecordRepository();
        ModelCheckRecordRepository modelCheckRepository = mock(ModelCheckRecordRepository.class);
        when(modelCheckRepository.listAll()).thenReturn(List.of());
        ObservationStatsAggregator statsAggregator = new ObservationStatsAggregator(
                executionRepository, usageRepository
        );
        queryService = new RuntimeObservationQueryService(
                executionRepository, usageRepository, modelCheckRepository, statsAggregator
        );
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void aggregateStats_shouldGroupByCapabilityModelAndTool() {
        Instant t1 = Instant.parse("2026-06-10T10:00:00Z");
        Instant t2 = Instant.parse("2026-06-10T11:00:00Z");

        executionRepository.save(new ExecutionRecord(
                "trace-1", "capability.qa.answer", "agent.qa", "TOOL_INVOKED", true, null, t1
        ));
        executionRepository.save(new ExecutionRecord(
                "trace-2", "capability.chat.generate", "agent.chat", "FAILED", false, "timeout", t2
        ));

        usageRepository.save(new UsageRecord(
                "trace-1", "capability.qa.answer", "agent.qa", "TOOL", null, "tool.search",
                0, 0, 0, BigDecimal.ZERO, t1
        ));
        usageRepository.save(new UsageRecord(
                "trace-2", "capability.chat.generate", "agent.chat", "LLM", "gpt-4o-mini", null,
                10, 20, 30, BigDecimal.ONE, t2
        ));

        ObservationStatsResult result = queryService.aggregateStats(
                Instant.parse("2026-06-10T09:00:00Z"),
                Instant.parse("2026-06-10T12:00:00Z")
        );

        assertEquals(2, result.summary().totalExecutions());
        assertEquals(1, result.summary().successExecutions());
        assertEquals(1, result.summary().failedExecutions());
        assertEquals(30, result.summary().totalTokens());
        assertEquals(2, result.byCapability().size());
        assertEquals("capability.qa.answer", result.byCapability().get(0).dimensionKey());
        assertEquals(1, result.byModel().size());
        assertEquals("gpt-4o-mini", result.byModel().get(0).dimensionKey());
        assertEquals(1, result.byTool().size());
        assertEquals("tool.search", result.byTool().get(0).dimensionKey());
    }

    @Test
    void aggregateStats_shouldFilterByTimeWindow() {
        executionRepository.save(new ExecutionRecord(
                "trace-old", "capability.qa.answer", "agent.qa", "TOOL_INVOKED", true, null,
                Instant.parse("2026-06-09T10:00:00Z")
        ));
        executionRepository.save(new ExecutionRecord(
                "trace-new", "capability.qa.answer", "agent.qa", "TOOL_INVOKED", true, null,
                Instant.parse("2026-06-10T10:00:00Z")
        ));

        ObservationStatsResult result = queryService.aggregateStats(
                Instant.parse("2026-06-10T00:00:00Z"),
                Instant.parse("2026-06-10T23:59:59Z")
        );

        assertEquals(1, result.summary().totalExecutions());
        assertTrue(result.byCapability().stream()
                .allMatch(item -> item.invocationCount() == 1));
    }

    @Test
    void aggregateStats_shouldOnlyIncludeCurrentTenantRecords() {
        TenantContext.setTenantCode("tenant-a");
        executionRepository.save(new ExecutionRecord(
                "trace-a", TenantContext.currentTenantCode(), "capability.qa.answer", "agent.qa",
                "TOOL_INVOKED", true, null, Instant.parse("2026-06-10T10:00:00Z"), null, null, null
        ));
        usageRepository.save(new UsageRecord(
                "trace-a", TenantContext.currentTenantCode(), "capability.qa.answer", "agent.qa", "TOOL",
                null, "tool.search", 0, 0, 0, BigDecimal.ZERO, Instant.parse("2026-06-10T10:00:00Z")
        ));
        TenantContext.setTenantCode("tenant-b");
        executionRepository.save(new ExecutionRecord(
                "trace-b", TenantContext.currentTenantCode(), "capability.qa.answer", "agent.qa",
                "TOOL_INVOKED", true, null, Instant.parse("2026-06-10T10:00:00Z"), null, null, null
        ));

        ObservationStatsResult result = queryService.aggregateStats(
                Instant.parse("2026-06-10T00:00:00Z"),
                Instant.parse("2026-06-10T23:59:59Z")
        );

        assertEquals(1, result.summary().totalExecutions());
        assertTrue(result.byTool().isEmpty());
    }
}
