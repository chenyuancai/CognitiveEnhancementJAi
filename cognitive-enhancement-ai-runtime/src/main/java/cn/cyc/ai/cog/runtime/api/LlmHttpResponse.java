package cn.cyc.ai.cog.runtime.api;

/**
 * LLM HTTP 响应对象。
 *
 * @param statusCode HTTP 状态码
 * @param body       响应体
 * @author cyc
 */
public record LlmHttpResponse(int statusCode, String body) {
}
