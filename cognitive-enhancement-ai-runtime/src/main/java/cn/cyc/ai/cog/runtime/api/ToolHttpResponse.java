package cn.cyc.ai.cog.runtime.api;

/**
 * HTTP Tool 调用响应。
 *
 * @param statusCode HTTP 状态码
 * @param body       响应体
 * @param latencyMs  耗时（毫秒）
 * @author cyc
 */
public record ToolHttpResponse(int statusCode, String body, long latencyMs) {
}
