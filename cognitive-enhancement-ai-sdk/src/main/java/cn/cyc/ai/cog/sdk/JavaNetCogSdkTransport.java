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
 */
final class JavaNetCogSdkTransport implements CogSdkTransport {

    private final HttpClient httpClient;

    JavaNetCogSdkTransport(CogSdkClientConfig config) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(config.timeout())
                .build();
    }

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
