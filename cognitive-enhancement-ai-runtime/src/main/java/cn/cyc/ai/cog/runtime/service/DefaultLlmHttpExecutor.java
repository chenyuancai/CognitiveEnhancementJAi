package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.api.LlmHttpRequest;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * 默认 LLM HTTP 执行器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class DefaultLlmHttpExecutor implements LlmHttpExecutor {

    /**
     * HTTP 执行日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultLlmHttpExecutor.class);

    /**
     * JDK HTTP 客户端。
     */
    private final HttpClient httpClient;

    /**
     * 构造默认执行器。
     */
    public DefaultLlmHttpExecutor() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    /**
     * 执行一次 HTTP 请求。
     *
     * @param request HTTP 请求
     * @return HTTP 响应
     */
    @Override
    public LlmHttpResponse execute(LlmHttpRequest request) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(request.url()))
                    .timeout(request.timeout())
                    .header("Content-Type", "application/json");
            for (Map.Entry<String, String> headerEntry : request.headers().entrySet()) {
                if ("Content-Type".equalsIgnoreCase(headerEntry.getKey())) {
                    continue;
                }
                builder.header(headerEntry.getKey(), headerEntry.getValue());
            }
            HttpRequest httpRequest = builder.POST(HttpRequest.BodyPublishers.ofString(request.body())).build();
            log.info("执行 LLM HTTP 请求, url={}, timeoutMs={}", request.url(), request.timeout().toMillis());
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return new LlmHttpResponse(response.statusCode(), response.body());
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new BusinessException("CONFLICT", "LLM HTTP 调用失败: " + exception.getMessage(), exception);
        }
    }
}
