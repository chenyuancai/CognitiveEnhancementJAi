package cn.cyc.ai.cog.sdk;

import java.util.Map;

/**
 * SDK 能力执行结果摘要。
 *
 * @param traceId 执行链路 ID
 * @param status  执行结果状态
 * @param message 执行结果说明
 * @param output  执行输出
 * @author cyc
 */
public record CapabilityExecutionResult(String traceId,
                                        String status,
                                        String message,
                                        Map<String, Object> output) {

    public CapabilityExecutionResult {
        output = Map.copyOf(output == null ? Map.of() : output);
    }
}
