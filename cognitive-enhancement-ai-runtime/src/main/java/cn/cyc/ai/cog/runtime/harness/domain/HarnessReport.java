package cn.cyc.ai.cog.runtime.harness.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Harness 完整执行报告。
 *
 * @param harnessId       Harness 执行唯一标识
 * @param traceId         链路追踪 ID
 * @param status          整体状态：RUNNING / PASSED / PARTIAL / FAILED
 * @param startTime       开始时间
 * @param endTime         结束时间
 * @param totalDurationMs 总耗时（毫秒）
 * @param scenario        场景摘要
 * @param steps           各步骤结果
 * @param summary         执行摘要
 * @author cyc
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
