package cn.cyc.ai.cog.runtime.api;

import java.time.Duration;
import java.util.Map;

/**
 * LLM HTTP 请求对象。
 *
 * @param url     请求地址
 * @param headers 请求头
 * @param body    请求体
 * @param timeout 请求超时时间
 * @author cyc
 */
public record LlmHttpRequest(String url,
                             Map<String, String> headers,
                             String body,
                             Duration timeout) {
}
