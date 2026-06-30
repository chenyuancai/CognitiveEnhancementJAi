package cn.cyc.ai.cog.sdk;

import java.time.Duration;
import java.util.Map;

/**
 * SDK 内部 HTTP 请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
record SdkHttpRequest(String method,
                      String url,
                      Map<String, String> headers,
                      String body,
                      Duration timeout) {

    SdkHttpRequest {
        headers = Map.copyOf(headers == null ? Map.of() : headers);
    }
}
