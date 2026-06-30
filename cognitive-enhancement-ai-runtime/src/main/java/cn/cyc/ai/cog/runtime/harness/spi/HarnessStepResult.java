package cn.cyc.ai.cog.runtime.harness.spi;

import java.util.Map;

/**
 * Harness 步骤执行结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record HarnessStepResult(
        String stepCode,
        String stepName,
        boolean passed,
        long durationMs,
        String message,
        Map<String, Object> details
) {
}
