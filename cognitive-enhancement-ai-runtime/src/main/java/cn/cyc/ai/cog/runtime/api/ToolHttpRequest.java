package cn.cyc.ai.cog.runtime.api;

import java.time.Duration;
import java.util.Map;

/**
 * HTTP Tool 调用请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ToolHttpRequest(
        String url,
        String method,
        Map<String, String> headers,
        String body,
        Duration timeout
) {
}
