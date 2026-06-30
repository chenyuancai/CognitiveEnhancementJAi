package cn.cyc.ai.cog.gateway.filter;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import cn.cyc.ai.cog.gateway.support.GatewayCompositeBearerIdentityParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 身份透传（方案 B）：将 JWT 解析为内部请求头。方案 A 下不启用。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@ConditionalOnProperty(name = "cog.gateway.api-auth.enabled", havingValue = "true")
public class IdentityRelayGlobalFilter implements GlobalFilter, Ordered {

    /** compositeBearerIdentityParser。 */
    private final GatewayCompositeBearerIdentityParser compositeBearerIdentityParser;

    /**
     * 创建IdentityRelayGlobal过滤器。
     *
     * @param compositeBearerIdentityParser compositeBearerIdentityParser
     */
    public IdentityRelayGlobalFilter(GatewayCompositeBearerIdentityParser compositeBearerIdentityParser) {
        this.compositeBearerIdentityParser = compositeBearerIdentityParser;
    }

    /**
     * 执行过滤器。
     *
     * @param exchange exchange
     * @param chain chain
     * @return 执行结果
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        AuthUser verified = exchange.getAttribute(SecurityConstants.GATEWAY_AUTH_USER_ATTR);
        if (verified != null) {
            return chain.filter(mutate(exchange, verified));
        }
        return exchange.getPrincipal()
                .filter(JwtAuthenticationToken.class::isInstance)
                .cast(JwtAuthenticationToken.class)
                .map(token -> mutateFromOAuth2Jwt(exchange, token.getToken()))
                .switchIfEmpty(Mono.fromSupplier(() -> mutateFromLegacyBearer(exchange)))
                .flatMap(chain::filter);
    }

    /**
     * 执行mutateFromLegacyBearer。
     *
     * @param exchange exchange
     * @return 执行结果
     */
    private ServerWebExchange mutateFromLegacyBearer(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst(SecurityConstants.AUTHORIZATION_HEADER);
        AuthUser user = compositeBearerIdentityParser.parseBearer(authorization);
        if (user == null) {
            return exchange;
        }
        return mutate(exchange, user);
    }

    /**
     * 执行mutateFromOAuth2Jwt。
     *
     * @param exchange exchange
     * @param jwt jwt
     * @return 执行结果
     */
    private ServerWebExchange mutateFromOAuth2Jwt(ServerWebExchange exchange, Jwt jwt) {
        AuthUser user = compositeBearerIdentityParser.parseBearer(
                SecurityConstants.BEARER_PREFIX + jwt.getTokenValue());
        if (user == null) {
            user = new AuthUser(
                    parseLong(claimAsString(jwt, SecurityConstants.CLAIM_USER_ID)),
                    claimAsString(jwt, SecurityConstants.CLAIM_USERNAME),
                    claimAsString(jwt, SecurityConstants.CLAIM_TENANT),
                    claimAsCsv(jwt, SecurityConstants.CLAIM_ROLES),
                    claimAsCsv(jwt, SecurityConstants.CLAIM_AUTHORITIES));
        }
        return mutate(exchange, user);
    }

    /**
     * 执行mutate。
     *
     * @param exchange exchange
     * @param user 用户
     * @return 执行结果
     */
    private ServerWebExchange mutate(ServerWebExchange exchange, AuthUser user) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.set(SecurityConstants.HEADER_USER_ID, String.valueOf(user.getUserId()));
                    headers.set(SecurityConstants.HEADER_USERNAME, nullToEmpty(user.getUsername()));
                    headers.set(SecurityConstants.HEADER_TENANT, nullToEmpty(user.getTenantCode()));
                    if (user.getTenantId() != null) {
                        headers.set(SecurityConstants.HEADER_TENANT_ID, String.valueOf(user.getTenantId()));
                    }
                    headers.set(SecurityConstants.HEADER_ROLES, joinCsv(user.getRoles()));
                    headers.set(SecurityConstants.HEADER_AUTHORITIES, joinCsv(user.getAuthorities()));
                })
                .build();
        return exchange.mutate().request(request).build();
    }

    /**
     * 执行claimAsString。
     *
     * @param jwt jwt
     * @param name 名称
     * @return 执行结果
     */
    private String claimAsString(Jwt jwt, String name) {
        Object value = jwt.getClaim(name);
        return value == null ? "" : String.valueOf(value);
    }

    /**
     * 执行claimAsCsv。
     *
     * @param jwt jwt
     * @param name 名称
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    private List<String> claimAsCsv(Jwt jwt, String name) {
        Object value = jwt.getClaim(name);
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return value == null ? List.of() : List.of(String.valueOf(value));
    }

    /**
     * 执行joinCsv。
     *
     * @param values values
     * @return 执行结果
     */
    private String joinCsv(List<String> values) {
        return values == null || values.isEmpty() ? "" : String.join(",", values);
    }

    /**
     * 执行nullToEmpty。
     *
     * @param value 值
     * @return 执行结果
     */
    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * 执行parseLong。
     *
     * @param value 值
     * @return 执行结果
     */
    private Long parseLong(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取订单。
     * @return 订单
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
