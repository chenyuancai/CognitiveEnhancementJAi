package cn.cyc.ai.cog.runtime.tool.dto;

import java.util.Map;

/**
 * Tool 调试调用请求。
 *
 * @param input          Tool 输入
 * @param parameters     执行参数
 * @param traceId        调试链路 ID
 * @param debugConfirmed HIGH 风险调试确认标识
 * @author cyc
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
