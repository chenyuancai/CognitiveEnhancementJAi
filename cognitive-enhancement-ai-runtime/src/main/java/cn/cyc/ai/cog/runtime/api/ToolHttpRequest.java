package cn.cyc.ai.cog.runtime.api;

import java.time.Duration;
import java.util.Map;

/**
 * HTTP Tool 调用请求。
 *
 * @param url     目标 URL
 * @param method  HTTP 方法
 * @param headers 请求头
 * @param body    请求体
 * @param timeout 超时时间
 * @author cyc
 */
public record ToolHttpRequest(
        String url,
        String method,
        Map<String, String> headers,
        String body,
        Duration timeout
) {
}
