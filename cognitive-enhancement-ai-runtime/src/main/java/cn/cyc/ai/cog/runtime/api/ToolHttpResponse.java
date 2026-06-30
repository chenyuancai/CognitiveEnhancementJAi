package cn.cyc.ai.cog.runtime.api;

/**
 * HTTP Tool 调用响应。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ToolHttpResponse(int statusCode, String body, long latencyMs) {
}
