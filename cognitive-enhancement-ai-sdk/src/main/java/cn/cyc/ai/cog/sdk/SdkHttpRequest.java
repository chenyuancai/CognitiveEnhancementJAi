package cn.cyc.ai.cog.sdk;

import java.time.Duration;
import java.util.Map;

/**
 * SDK 内部 HTTP 请求。
 *
 * @param method  请求方法
 * @param url     请求地址
 * @param headers 请求头
 * @param body    请求体
 * @param timeout 超时时间
 * @author cyc
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
