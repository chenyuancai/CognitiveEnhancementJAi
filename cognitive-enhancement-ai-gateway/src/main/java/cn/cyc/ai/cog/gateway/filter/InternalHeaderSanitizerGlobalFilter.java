package cn.cyc.ai.cog.gateway.filter;

import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 剥离客户端伪造的内部身份头，避免绕过 JWT 验签直接冒充用户。
 */
@Component
public class InternalHeaderSanitizerGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(SecurityConstants.HEADER_USER_ID);
                    headers.remove(SecurityConstants.HEADER_USERNAME);
                    headers.remove(SecurityConstants.HEADER_TENANT);
                    headers.remove(SecurityConstants.HEADER_TENANT_ID);
                    headers.remove(SecurityConstants.HEADER_ROLES);
                    headers.remove(SecurityConstants.HEADER_AUTHORITIES);
                })
                .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
