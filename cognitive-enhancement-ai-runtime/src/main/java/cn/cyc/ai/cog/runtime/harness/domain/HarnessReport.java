package cn.cyc.ai.cog.runtime.harness.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Harness 完整执行报告。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record HarnessReport(
        String harnessId,
        String traceId,
        String status,
        Instant startTime,
        Instant endTime,
        long totalDurationMs,
        HarnessScenarioSummary scenario,
        List<HarnessStepReport> steps,
        HarnessSummary summary
) {

    /**
     * 场景摘要。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record HarnessScenarioSummary(
            String capabilityCode,
            String capabilityName,
            String agentCode,
            String agentName,
            List<String> skillCodes,
            List<String> toolCodes,
            String modelCode,
            String modelName,
            Map<String, Object> inputParams
    ) {
    }

    /**
     * 步骤报告。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record HarnessStepReport(
            int sequence,
            String stepCode,
            String stepName,
            String description,
            String status,
            long durationMs,
            String message,
            Map<String, Object> details
    ) {
    }

    /**
     * 执行摘要。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record HarnessSummary(
            int totalSteps,
            int passedSteps,
            int failedSteps,
            int skippedSteps,
            List<String> failedStepNames,
            String recommendation
    ) {
    }
}
