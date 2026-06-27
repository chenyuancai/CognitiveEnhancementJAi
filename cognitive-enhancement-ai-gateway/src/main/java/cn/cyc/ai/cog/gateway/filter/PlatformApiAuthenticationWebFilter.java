package cn.cyc.ai.cog.gateway.filter;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import cn.cyc.ai.cog.gateway.config.GatewayApiAuthProperties;
import cn.cyc.ai.cog.gateway.support.GatewayCompositeBearerIdentityParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * /api/** 强制 Bearer 验签（方案 B）。方案 A 纯转发时通过 {@code cog.gateway.api-auth.enabled=false} 关闭。
 */
@Component
@ConditionalOnProperty(name = "cog.gateway.api-auth.enabled", havingValue = "true")
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class PlatformApiAuthenticationWebFilter implements WebFilter {

    private final GatewayApiAuthProperties properties;
    private final GatewayCompositeBearerIdentityParser compositeBearerIdentityParser;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public PlatformApiAuthenticationWebFilter(GatewayApiAuthProperties properties,
                                            GatewayCompositeBearerIdentityParser compositeBearerIdentityParser,
                                            ObjectMapper objectMapper) {
        this.properties = properties;
        this.compositeBearerIdentityParser = compositeBearerIdentityParser;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        if (!path.startsWith("/api/") || isPermitAll(path)) {
            return chain.filter(exchange);
        }
        String authorization = exchange.getRequest().getHeaders().getFirst(SecurityConstants.AUTHORIZATION_HEADER);
        AuthUser user = compositeBearerIdentityParser.parseBearer(authorization);
        if (user == null) {
            return unauthorized(exchange);
        }
        exchange.getAttributes().put(SecurityConstants.GATEWAY_AUTH_USER_ATTR, user);
        return chain.filter(exchange);
    }

    private boolean isPermitAll(String path) {
        return properties.getPermitAll().stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("code", "C0401");
        body.put("message", "未认证或令牌无效");
        body.put("data", null);
        try {
            byte[] bytes = objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory()
                    .wrap(bytes)));
        } catch (Exception ex) {
            return exchange.getResponse().setComplete();
        }
    }
}
