package cn.cyc.ai.cog.sdk;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 基于 JDK HttpClient 的 SDK HTTP 传输实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
final class JavaNetCogSdkTransport implements CogSdkTransport {

    /** http客户端。 */
    private final HttpClient httpClient;

    /**
     * 创建JavaNetCogSdkTransport。
     *
     * @param config 配置
     */
    JavaNetCogSdkTransport(CogSdkClientConfig config) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(config.timeout())
                .build();
    }

    /**
     * 执行send。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Override
    public SdkHttpResponse send(SdkHttpRequest request) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(request.url()))
                .timeout(request.timeout());
        request.headers().forEach(builder::header);
        if ("POST".equalsIgnoreCase(request.method())) {
            builder.POST(HttpRequest.BodyPublishers.ofString(request.body()));
        } else {
            builder.GET();
        }
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return new SdkHttpResponse(response.statusCode(), response.body());
    }
}
