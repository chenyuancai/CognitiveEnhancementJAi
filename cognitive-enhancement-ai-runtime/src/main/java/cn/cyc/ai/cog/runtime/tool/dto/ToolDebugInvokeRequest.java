package cn.cyc.ai.cog.runtime.tool.dto;

import java.util.Map;

/**
 * Tool 调试调用请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ToolDebugInvokeRequest(
        String toolCode,
        Object input,
        Map<String, Object> parameters,
        String traceId,
        Boolean debugConfirmed
) {

    /**
     * 构造请求并收敛参数集合。
     */
    public ToolDebugInvokeRequest {
        parameters = Map.copyOf(parameters == null ? Map.of() : parameters);
    }
}
