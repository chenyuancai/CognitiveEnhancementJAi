package cn.cyc.ai.cog.runtime.harness.dto;

/**
 * Harness 场景模板（前端下拉选项）。
 *
 * @param name        模板名称
 * @param description 模板描述
 * @param scenario    场景配置
 * @author cyc
 */
public record HarnessScenarioTemplate(
        String name,
        String description,
        HarnessScenario scenario
) {
}
