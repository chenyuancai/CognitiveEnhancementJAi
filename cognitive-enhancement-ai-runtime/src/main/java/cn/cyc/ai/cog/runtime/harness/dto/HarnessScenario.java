package cn.cyc.ai.cog.runtime.harness.dto;

import java.util.List;
import java.util.Map;

/**
 * Harness 测试场景配置，由前端传入。
 *
 * @param capabilityCode 能力编码（可选）
 * @param agentCode      Agent 编码（必选）
 * @param skillCodes     技能编码列表（可选）
 * @param toolCodes      工具编码列表（可选）
 * @param modelCode      模型编码（可选）
 * @param inputParams    测试输入参数
 * @author cyc
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
