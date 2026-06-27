package cn.cyc.ai.cog.gateway.filter;

import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import cn.cyc.ai.cog.gateway.config.GatewayLogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 网关请求入参日志：打印方法、路径、Query、请求头与请求体（借鉴 zcloud GlobalRequestLogFilter）。
 *
 * <p>敏感头（Authorization/Cookie）脱敏；multipart/二进制请求体跳过打印。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "cog.gateway.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class GlobalRequestLogFilter implements GlobalFilter, Ordered {

    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            HttpHeaders.AUTHORIZATION.toLowerCase(),
            HttpHeaders.COOKIE.toLowerCase(),
            "x-api-key"
    );

    private static final Set<HttpMethod> BODY_METHODS = Set.of(
            HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE
    );

    private final GatewayLogProperties logProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().pathWithinApplication().value();
        if (isExcluded(path)) {
            return chain.filter(exchange);
        }

        if (!logProperties.isLogBody() || !shouldReadBody(request)) {
            logRequest(request, null);
            return chain.filter(exchange);
        }

        return DataBufferUtils.join(request.getBody())
                .defaultIfEmpty(exchange.getResponse().bufferFactory().allocateBuffer(0))
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    if (bytes.length > 0) {
                        dataBuffer.read(bytes);
                    }
                    DataBufferUtils.release(dataBuffer);

                    String body = bytes.length == 0 ? "" : new String(bytes, StandardCharsets.UTF_8);
                    logRequest(request, body);

                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            if (bytes.length == 0) {
                                return Flux.empty();
                            }
                            return Flux.defer(() -> Flux.just(exchange.getResponse().bufferFactory().wrap(bytes)));
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                });
    }

    private void logRequest(ServerHttpRequest request, String body) {
        StringBuilder logBuilder = new StringBuilder(512);
        logBuilder.append("\n================ Gateway Request Start ================\n");
        logBuilder.append("===> ").append(request.getMethod()).append(": ").append(request.getURI()).append('\n');

        if (logProperties.isLogQuery() && !request.getQueryParams().isEmpty()) {
            logBuilder.append("===Query===  ").append(formatQueryParams(request.getQueryParams())).append('\n');
        }

        if (logProperties.isLogHeaders()) {
            request.getHeaders().forEach((name, values) -> {
                String display = isSensitiveHeader(name) ? maskSensitiveValue(values) : String.join(",", values);
                logBuilder.append("===Headers===  ").append(name).append(": ").append(display).append('\n');
            });
        }

        if (body != null) {
            logBuilder.append("===Body===  ").append(truncate(body)).append('\n');
        }

        logBuilder.append("================ Gateway Request End =================");
        log.info(logBuilder.toString());
    }

    private String formatQueryParams(Map<String, List<String>> queryParams) {
        return queryParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private boolean shouldReadBody(ServerHttpRequest request) {
        if (!BODY_METHODS.contains(request.getMethod())) {
            return false;
        }
        MediaType contentType = request.getHeaders().getContentType();
        if (contentType == null) {
            return true;
        }
        if (MediaType.MULTIPART_FORM_DATA.includes(contentType)
                || MediaType.APPLICATION_OCTET_STREAM.includes(contentType)) {
            return false;
        }
        return MediaType.APPLICATION_JSON.includes(contentType)
                || MediaType.APPLICATION_FORM_URLENCODED.includes(contentType)
                || MediaType.TEXT_PLAIN.includes(contentType)
                || MediaType.TEXT_XML.includes(contentType)
                || "application".equalsIgnoreCase(contentType.getType())
                && contentType.getSubtype() != null
                && contentType.getSubtype().endsWith("+json");
    }

    private boolean isExcluded(String path) {
        for (String pattern : logProperties.getExcludePaths()) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSensitiveHeader(String headerName) {
        return SENSITIVE_HEADERS.contains(headerName.toLowerCase())
                || SecurityConstants.AUTHORIZATION_HEADER.equalsIgnoreCase(headerName);
    }

    private String maskSensitiveValue(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "***";
        }
        String value = values.get(0);
        if (!StringUtils.hasText(value)) {
            return "***";
        }
        if (value.startsWith(SecurityConstants.BEARER_PREFIX)) {
            return SecurityConstants.BEARER_PREFIX + "***";
        }
        if (value.length() <= 8) {
            return "***";
        }
        return value.substring(0, 4) + "***" + value.substring(value.length() - 4);
    }

    private String truncate(String body) {
        int max = Math.max(logProperties.getMaxBodyLength(), 0);
        if (body.length() <= max) {
            return body;
        }
        return body.substring(0, max) + "...(truncated)";
    }

    @Override
    public int getOrder() {
        // 紧随内部头清洗之后，尽早记录客户端原始入参
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
