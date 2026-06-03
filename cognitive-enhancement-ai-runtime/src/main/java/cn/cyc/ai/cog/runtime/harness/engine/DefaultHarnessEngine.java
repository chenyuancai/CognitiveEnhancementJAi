package cn.cyc.ai.cog.runtime.harness.engine;

import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessEngine;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Harness 默认执行引擎。
 *
 * @author cyc
 */
@Component
public class DefaultHarnessEngine implements HarnessEngine {

    private static final Logger log = LoggerFactory.getLogger(DefaultHarnessEngine.class);

    @Override
    public HarnessReport run(List<HarnessStep> steps, HarnessContext context, Consumer<HarnessReport.HarnessStepReport> stepCallback) {
        log.info("开始 Harness 执行, harnessId={}, traceId={}", context.harnessId(), context.traceId());
        Instant startTime = Instant.now();
        List<HarnessReport.HarnessStepReport> stepReports = new ArrayList<>();
        boolean anyFailed = false;

        for (int i = 0; i < steps.size(); i++) {
            HarnessStep step = steps.get(i);

            if (anyFailed) {
                HarnessReport.HarnessStepReport stepReport = new HarnessReport.HarnessStepReport(
                        i + 1, step.stepCode(), step.stepName(), step.description(),
                        "SKIPPED", 0, "前置步骤失败，已跳过", Map.of()
                );
                stepReports.add(stepReport);
                if (stepCallback != null) {
                    stepCallback.accept(stepReport);
                }
                continue;
            }

            long stepStart = System.currentTimeMillis();
            HarnessStepResult result;
            try {
                result = step.run(context);
            } catch (Exception ex) {
                log.error("Harness 步骤执行异常, harnessId={}, stepCode={}, stepName={}",
                        context.harnessId(), step.stepCode(), step.stepName(), ex);
                result = new HarnessStepResult(
                        step.stepCode(), step.stepName(), false,
                        System.currentTimeMillis() - stepStart,
                        "步骤执行异常: " + ex.getMessage(), Map.of("exception", ex.getClass().getSimpleName())
                );
            }
            long durationMs = System.currentTimeMillis() - stepStart;

            if (!result.passed()) {
                anyFailed = true;
                log.error("Harness 步骤未通过, harnessId={}, stepCode={}, stepName={}, message={}",
                        context.harnessId(), result.stepCode(), result.stepName(), result.message());
            }

            HarnessReport.HarnessStepReport stepReport = new HarnessReport.HarnessStepReport(
                    i + 1, result.stepCode(), result.stepName(), step.description(),
                    result.passed() ? "PASSED" : "FAILED", durationMs,
                    result.message(), result.details()
            );
            stepReports.add(stepReport);
            if (stepCallback != null) {
                stepCallback.accept(stepReport);
            }
        }

        Instant endTime = Instant.now();
        long totalDurationMs = endTime.toEpochMilli() - startTime.toEpochMilli();
        String overallStatus = anyFailed ? "FAILED" : "PASSED";

        int passedCount = (int) stepReports.stream().filter(s -> "PASSED".equals(s.status())).count();
        int failedCount = (int) stepReports.stream().filter(s -> "FAILED".equals(s.status())).count();
        int skippedCount = (int) stepReports.stream().filter(s -> "SKIPPED".equals(s.status())).count();

        List<String> failedNames = stepReports.stream()
                .filter(s -> "FAILED".equals(s.status()))
                .map(HarnessReport.HarnessStepReport::stepName)
                .toList();

        String recommendation = failedNames.isEmpty() ? "全部通过" : "请检查失败步骤: " + String.join(", ", failedNames);

        HarnessReport.HarnessSummary summary = new HarnessReport.HarnessSummary(
                steps.size(), passedCount, failedCount, skippedCount, failedNames, recommendation
        );

        HarnessReport.HarnessScenarioSummary scenarioSummary = buildScenarioSummary(context);

        log.info("Harness 执行完成, harnessId={}, status={}, passed={}/{}",
                context.harnessId(), overallStatus, passedCount, steps.size());

        return new HarnessReport(
                context.harnessId(), context.traceId(), overallStatus,
                startTime, endTime, totalDurationMs, scenarioSummary, stepReports, summary
        );
    }

    private HarnessReport.HarnessScenarioSummary buildScenarioSummary(HarnessContext context) {
        return new HarnessReport.HarnessScenarioSummary(
                context.scenario() != null ? context.scenario().capabilityCode() : null,
                context.capability() != null ? context.capability().capabilityName() : null,
                context.scenario() != null ? context.scenario().agentCode() : null,
                context.agent() != null ? context.agent().agentName() : null,
                context.skills() != null ? context.skills().stream().map(SkillDefinition::skillCode).toList() : List.of(),
                context.tools() != null ? context.tools().stream().map(ToolDefinition::toolCode).toList() : List.of(),
                context.scenario() != null ? context.scenario().modelCode() : null,
                context.model() != null ? context.model().modelName() : null,
                context.scenario() != null ? context.scenario().inputParams() : Map.of()
        );
    }
}
