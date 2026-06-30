package cn.cyc.ai.cog.runtime.harness.dto;

import java.util.List;
import java.util.Map;

/**
 * Harness 测试场景配置，由前端传入。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record HarnessScenario(
        String capabilityCode,
        String agentCode,
        List<String> skillCodes,
        List<String> toolCodes,
        String modelCode,
        Map<String, Object> inputParams
) {
}
