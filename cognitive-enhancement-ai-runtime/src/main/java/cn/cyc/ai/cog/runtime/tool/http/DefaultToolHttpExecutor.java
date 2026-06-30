package cn.cyc.ai.cog.runtime.tool.http;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.api.ToolHttpRequest;
import cn.cyc.ai.cog.runtime.api.ToolHttpResponse;
import cn.cyc.ai.cog.runtime.tool.spi.ToolHttpExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;

/**
 * 默认 HTTP Tool 执行器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class DefaultToolHttpExecutor implements ToolHttpExecutor {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(DefaultToolHttpExecutor.class);

    /** http客户端。 */
    private final HttpClient httpClient;

    /**
     * 创建DefaultToolHttpExecutor。
     */
    public DefaultToolHttpExecutor() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    /**
     * 执行操作。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Override
    public ToolHttpResponse execute(ToolHttpRequest request) {
        long start = System.currentTimeMillis();
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(request.url()))
                    .timeout(request.timeout());
            for (Map.Entry<String, String> header : request.headers().entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }
            HttpRequest httpRequest = buildHttpRequest(builder, request);
            log.info("执行 HTTP Tool 请求, url={}, method={}, timeoutMs={}",
                    request.url(), request.method(), request.timeout().toMillis());
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            long latencyMs = System.currentTimeMillis() - start;
            return new ToolHttpResponse(response.statusCode(), response.body(), latencyMs);
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new BusinessException("CONFLICT", "HTTP Tool 调用失败: " + exception.getMessage(), exception);
        }
    }

    /**
     * 构建Http请求。
     *
     * @param builder 构建器
     * @param request 请求
     * @return 构建结果
     */
    private HttpRequest buildHttpRequest(HttpRequest.Builder builder, ToolHttpRequest request) {
        String method = request.method() == null ? "POST" : request.method().toUpperCase(Locale.ROOT);
        return switch (method) {
            case "GET" -> builder.GET().build();
            case "POST" -> builder.header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(request.body() == null ? "" : request.body()))
                    .build();
            case "PUT" -> builder.header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(request.body() == null ? "" : request.body()))
                    .build();
            default -> throw new BusinessException("INVALID_ARGUMENT", "不支持的 HTTP 方法: " + method);
        };
    }
}
