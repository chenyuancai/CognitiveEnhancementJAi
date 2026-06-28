package cn.cyc.ai.cog.runtime.harness.engine;

import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultHarnessEngineCallbackTest {

    @Test
    void run_withCallback_shouldInvokePerStep() {
        List<HarnessReport.HarnessStepReport> callbacks = new ArrayList<>();
        Consumer<HarnessReport.HarnessStepReport> callback = callbacks::add;

        HarnessStep step = mock(HarnessStep.class);
        when(step.stepCode()).thenReturn("TEST");
        when(step.stepName()).thenReturn("测试步骤");
        when(step.description()).thenReturn("测试");
        when(step.run(any())).thenReturn(new HarnessStepResult(
                "TEST", "测试步骤", true, 10, "通过", Map.of()
        ));

        DefaultHarnessEngine engine = new DefaultHarnessEngine(mock(TraceSpanRecorder.class));
        HarnessContext ctx = new HarnessContext(
                UUID.randomUUID().toString(), "trace-1", Instant.now(),
                null, null, null, null, null, null, Map.of()
        );

        HarnessReport report = engine.run(List.of(step), ctx, callback);

        assertEquals("PASSED", report.status());
        assertEquals(1, callbacks.size());
        assertEquals("TEST", callbacks.get(0).stepCode());
    }

    @Test
    void run_withFailedStep_shouldStillInvokeCallback() {
        List<HarnessReport.HarnessStepReport> callbacks = new ArrayList<>();
        Consumer<HarnessReport.HarnessStepReport> callback = callbacks::add;

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

        DefaultHarnessEngine engine = new DefaultHarnessEngine(mock(TraceSpanRecorder.class));
        HarnessContext ctx = new HarnessContext(
                UUID.randomUUID().toString(), "trace-1", Instant.now(),
                null, null, null, null, null, null, Map.of()
        );

        HarnessReport report = engine.run(List.of(failStep, skipStep), ctx, callback);

        assertEquals("FAILED", report.status());
        assertEquals(2, callbacks.size());
        assertEquals("FAILED", callbacks.get(0).status());
        assertEquals("SKIPPED", callbacks.get(1).status());
    }
}
