package cn.cyc.ai.cog.gateway.config;

import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * 限流相关配置：提供限流维度 Key 解析器。
 * <p>预置「按用户」与「按 IP」两种 KeyResolver，配合 Redis RequestRateLimiter（在路由中启用）
 * 即可对网关流量做令牌桶限流。当前仅提供 Key 解析，限流规则在 application.yml 路由上按需开启。</p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
public class RateLimitConfig {

    /** 按用户限流：优先用网关解析出的用户头，匿名则回退到 IP。作为 Gateway 限流默认 KeyResolver。 */
    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders()
                    .getFirst(SecurityConstants.HEADER_USER_ID);
            if (StringUtils.hasText(userId)) {
                return Mono.just("user:" + userId);
            }
            return Mono.just("ip:" + clientIp(exchange));
        };
    }

    /** 按客户端 IP 限流。 */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just("ip:" + clientIp(exchange));
    }

    /**
     * 执行客户端Ip。
     *
     * @param exchange exchange
     * @return 执行结果
     */
    private String clientIp(org.springframework.web.server.ServerWebExchange exchange) {
        if (exchange.getRequest().getRemoteAddress() == null) {
            return "unknown";
        }
        return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }
}
