package cn.cyc.ai.cog.sdk;

/**
 * SDK 内部 HTTP 响应。
 *
 * @param statusCode HTTP 状态码
 * @param body       响应体
 * @author cyc
 */
record SdkHttpResponse(int statusCode, String body) {
}
