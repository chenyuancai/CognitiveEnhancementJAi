package cn.cyc.ai.cog.runtime.harness.dto;

/**
 * Harness 场景模板（前端下拉选项）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record HarnessScenarioTemplate(
        String name,
        String description,
        HarnessScenario scenario
) {
}
