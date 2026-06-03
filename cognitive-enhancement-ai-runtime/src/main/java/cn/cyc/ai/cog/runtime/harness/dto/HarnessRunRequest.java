package cn.cyc.ai.cog.runtime.harness.dto;

/**
 * Harness 执行请求。
 *
 * @param scenario 测试场景配置
 * @author cyc
 */
public record HarnessRunRequest(HarnessScenario scenario) {
}
