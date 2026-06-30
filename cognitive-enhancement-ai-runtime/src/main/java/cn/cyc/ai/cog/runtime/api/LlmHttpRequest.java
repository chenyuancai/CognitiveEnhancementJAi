package cn.cyc.ai.cog.runtime.api;

import java.time.Duration;
import java.util.Map;

/**
 * LLM HTTP 请求对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record LlmHttpRequest(String url,
                             Map<String, String> headers,
                             String body,
                             Duration timeout) {
}
