package cn.cyc.ai.cog.sdk;

import java.util.Map;

/**
 * SDK 能力执行结果摘要。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityExecutionResult(String traceId,
                                        String status,
                                        String message,
                                        Map<String, Object> output) {

    public CapabilityExecutionResult {
        output = Map.copyOf(output == null ? Map.of() : output);
    }
}
