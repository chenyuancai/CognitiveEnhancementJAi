package cn.cyc.ai.cog.runtime.harness.repository;

import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.entity.HarnessReportEntity;
import cn.cyc.ai.cog.runtime.harness.entity.HarnessStepReportEntity;
import cn.cyc.ai.cog.runtime.harness.service.IHarnessReportService;
import cn.cyc.ai.cog.runtime.harness.service.IHarnessStepReportService;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersistentHarnessReportRepositoryTest {

    @Mock
    private IHarnessReportService reportService;

    @Mock
    private IHarnessStepReportService stepReportService;

    private ObjectMapper objectMapper;
    private PersistentHarnessReportRepository repository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        repository = new PersistentHarnessReportRepository(reportService, stepReportService, objectMapper);
    }

    @Test
    void findById_shouldRestoreScenarioStepsAndSummary() throws Exception {
        HarnessReport.HarnessScenarioSummary scenario = new HarnessReport.HarnessScenarioSummary(
                "capability.qa.answer", "问答能力", "agent.qa", "问答 Agent",
                List.of("skill.qa"), List.of("tool.search"), "qwen-plus", "通义千问",
                Map.of("question", "hello")
        );
        HarnessReport.HarnessSummary summary = new HarnessReport.HarnessSummary(
                2, 1, 0, 1, List.of(), "执行已取消"
        );

        HarnessReportEntity entity = new HarnessReportEntity();
        entity.setHarnessId("HAR-001");
        entity.setTraceId("trace-001");
        entity.setStatus("CANCELLED");
        entity.setStartTime(Instant.parse("2026-06-09T10:00:00Z"));
        entity.setEndTime(Instant.parse("2026-06-09T10:00:05Z"));
        entity.setTotalDurationMs(5000L);
        entity.setScenarioJson(objectMapper.writeValueAsString(scenario));
        entity.setSummaryJson(objectMapper.writeValueAsString(summary));

        HarnessStepReportEntity step1 = new HarnessStepReportEntity();
        step1.setHarnessId("HAR-001");
        step1.setSequence(1);
        step1.setStepCode("STEP1");
        step1.setStepName("步骤1");
        step1.setStatus("PASSED");
        step1.setDurationMs(10L);
        step1.setMessage("通过");
        step1.setDetailsJson(objectMapper.writeValueAsString(Map.of("ok", true)));

        HarnessStepReportEntity step2 = new HarnessStepReportEntity();
        step2.setHarnessId("HAR-001");
        step2.setSequence(2);
        step2.setStepCode("STEP2");
        step2.setStepName("步骤2");
        step2.setStatus("SKIPPED");
        step2.setDurationMs(0L);
        step2.setMessage("用户取消执行");

        @SuppressWarnings("unchecked")
        LambdaQueryChainWrapper<HarnessStepReportEntity> chain = mock(LambdaQueryChainWrapper.class);
        when(stepReportService.lambdaQuery()).thenReturn(chain);
        when(chain.eq(ArgumentMatchers.<SFunction<HarnessStepReportEntity, ?>>any(), any())).thenReturn(chain);
        when(chain.orderByAsc(ArgumentMatchers.<SFunction<HarnessStepReportEntity, ?>>any())).thenReturn(chain);
        when(chain.list()).thenReturn(List.of(step1, step2));
        when(reportService.getByHarnessId("HAR-001")).thenReturn(entity);

        Optional<HarnessReport> found = repository.findById("HAR-001");

        assertTrue(found.isPresent());
        HarnessReport report = found.get();
        assertEquals("CANCELLED", report.status());
        assertEquals("capability.qa.answer", report.scenario().capabilityCode());
        assertEquals(1, report.summary().passedSteps());
        assertEquals(2, report.steps().size());
        assertEquals("STEP1", report.steps().get(0).stepCode());
        assertEquals("SKIPPED", report.steps().get(1).status());
        assertEquals(true, report.steps().get(0).details().get("ok"));
    }
}
