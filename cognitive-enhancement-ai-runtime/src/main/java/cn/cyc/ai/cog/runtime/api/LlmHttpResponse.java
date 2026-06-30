package cn.cyc.ai.cog.runtime.api;

/**
 * LLM HTTP 响应对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record LlmHttpResponse(int statusCode, String body) {
}
