package cn.cyc.ai.cog.sdk;

/**
 * SDK 内部 HTTP 响应。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
record SdkHttpResponse(int statusCode, String body) {
}
