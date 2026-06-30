package cn.cyc.ai.cog.core.runtime;

import java.util.Map;
import java.util.Objects;

/**
 * 能力执行入口请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityExecuteRequest(
        String capabilityCode,
        Map<String, Object> input,
        Map<String, Object> parameters
) {

    /**
     * 构造能力执行请求并完成最小入参保护。
     */
    public CapabilityExecuteRequest {
        capabilityCode = Objects.requireNonNull(capabilityCode, "capabilityCode 不能为空");
        input = Map.copyOf(input == null ? Map.of() : input);
        parameters = Map.copyOf(parameters == null ? Map.of() : parameters);
    }
}
