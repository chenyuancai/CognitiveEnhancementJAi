package cn.cyc.ai.cog.runtime.harness.engine;

import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessCancellation;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultHarnessEngineTest {

    @AfterEach
    void tearDown() {
        TraceContext.clear();
    }

    private DefaultHarnessEngine newEngine() {
        return new DefaultHarnessEngine(mock(TraceSpanRecorder.class));
    }

    @Test
    void run_allStepsPassed_shouldReturnPassedReport() {
        HarnessStep step = mock(HarnessStep.class);
        when(step.stepCode()).thenReturn("TEST");
        when(step.stepName()).thenReturn("测试步骤");
        when(step.description()).thenReturn("测试");
        when(step.run(any())).thenReturn(new HarnessStepResult(
                "TEST", "测试步骤", true, 10, "通过", Map.of()
        ));

        DefaultHarnessEngine engine = newEngine();
        HarnessContext ctx = new HarnessContext(
                UUID.randomUUID().toString(), "trace-1", Instant.now(),
                null, null, null, null, null, null, Map.of()
        );

        HarnessReport report = engine.run(List.of(step), ctx);

        assertEquals("PASSED", report.status());
        assertEquals(1, report.summary().passedSteps());
        assertEquals(0, report.summary().failedSteps());
    }

    @Test
    void run_stepFailed_shouldSkipRemaining() {
        HarnessStep passStep = mock(HarnessStep.class);
        when(passStep.stepCode()).thenReturn("PASS");
        when(passStep.stepName()).thenReturn("通过步骤");
        when(passStep.description()).thenReturn("通过");
        when(passStep.run(any())).thenReturn(new HarnessStepResult(
                "PASS", "通过步骤", true, 10, "通过", Map.of()
        ));

        HarnessStep failStep = mock(HarnessStep.class);
        when(failStep.stepCode()).thenReturn("FAIL");
        when(failStep.stepName()).thenReturn("失败步骤");
        when(failStep.description()).thenReturn("失败");
        when(failStep.run(any())).thenReturn(new HarnessStepResult(
                "FAIL", "失败步骤", false, 10, "失败", Map.of()
        ));

        HarnessStep skipStep = mock(HarnessStep.class);
        when(skipStep.stepCode()).thenReturn("SKIP");
        when(skipStep.stepName()).thenReturn("跳过步骤");
        when(skipStep.description()).thenReturn("跳过");

        DefaultHarnessEngine engine = newEngine();
        HarnessContext ctx = new HarnessContext(
                UUID.randomUUID().toString(), "trace-1", Instant.now(),
                null, null, null, null, null, null, Map.of()
        );

        HarnessReport report = engine.run(List.of(passStep, failStep, skipStep), ctx);

        assertEquals("FAILED", report.status());
        assertEquals(1, report.summary().passedSteps());
        assertEquals(1, report.summary().failedSteps());
        assertEquals(1, report.summary().skippedSteps());
        verify(skipStep, never()).run(any());
    }

    @Test
    void run_cancelledAfterFirstStep_shouldSkipRemainingAndReturnCancelled() {
        HarnessStep step1 = mock(HarnessStep.class);
        when(step1.stepCode()).thenReturn("STEP1");
        when(step1.stepName()).thenReturn("步骤1");
        when(step1.description()).thenReturn("步骤1");
        when(step1.run(any())).thenReturn(new HarnessStepResult(
                "STEP1", "步骤1", true, 10, "通过", Map.of()
        ));

        HarnessStep step2 = mock(HarnessStep.class);
        when(step2.stepCode()).thenReturn("STEP2");
        when(step2.stepName()).thenReturn("步骤2");
        when(step2.description()).thenReturn("步骤2");

        HarnessStep step3 = mock(HarnessStep.class);
        when(step3.stepCode()).thenReturn("STEP3");
        when(step3.stepName()).thenReturn("步骤3");
        when(step3.description()).thenReturn("步骤3");

        HarnessCancellation cancellation = HarnessCancellation.create();
        DefaultHarnessEngine engine = newEngine();
        HarnessContext ctx = new HarnessContext(
                UUID.randomUUID().toString(), "trace-1", Instant.now(),
                null, null, null, null, null, null, Map.of()
        );

        HarnessReport report = engine.run(List.of(step1, step2, step3), ctx, stepReport -> {
            if ("STEP1".equals(stepReport.stepCode())) {
                cancellation.cancel();
            }
        }, cancellation);

        assertEquals("CANCELLED", report.status());
        assertEquals(1, report.summary().passedSteps());
        assertEquals(0, report.summary().failedSteps());
        assertEquals(2, report.summary().skippedSteps());
        assertEquals("执行已取消", report.summary().recommendation());
        verify(step1, times(1)).run(any());
        verify(step2, never()).run(any());
        verify(step3, never()).run(any());
    }

    @Test
    void run_shouldBindTraceContextDuringStepsAndClearAfterRun() {
        HarnessStep step = mock(HarnessStep.class);
        when(step.stepCode()).thenReturn("TEST");
        when(step.stepName()).thenReturn("测试步骤");
        when(step.description()).thenReturn("测试");
        when(step.run(any())).thenAnswer(invocation -> {
            assertEquals("trace-1", TraceContext.getTraceId());
            return new HarnessStepResult("TEST", "测试步骤", true, 10, "通过", Map.of());
        });

        DefaultHarnessEngine engine = newEngine();
        HarnessContext ctx = new HarnessContext(
                "HAR-test", "trace-1", Instant.now(),
                null, null, null, null, null, null, Map.of()
        );

        HarnessReport report = engine.run(List.of(step), ctx);

        assertEquals("trace-1", report.traceId());
        assertNull(TraceContext.getTraceId());
    }

    @Test
    void run_shouldFallbackTraceIdToHarnessIdWhenMissing() {
        HarnessStep step = mock(HarnessStep.class);
        when(step.stepCode()).thenReturn("TEST");
        when(step.stepName()).thenReturn("测试步骤");
        when(step.description()).thenReturn("测试");
        when(step.run(any())).thenAnswer(invocation -> {
            assertEquals("HAR-test", TraceContext.getTraceId());
            return new HarnessStepResult("TEST", "测试步骤", true, 10, "通过", Map.of());
        });

        DefaultHarnessEngine engine = newEngine();
        HarnessContext ctx = new HarnessContext(
                "HAR-test", null, Instant.now(),
                null, null, null, null, null, null, Map.of()
        );

        HarnessReport report = engine.run(List.of(step), ctx);

        assertEquals("HAR-test", report.traceId());
    }
}
