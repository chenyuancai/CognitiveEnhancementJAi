package cn.cyc.ai.cog.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * 网关侧阻塞式 {@link JwtDecoder}，供 {@code CompositeBearerIdentityParser} 验签 OAuth2 RS256 令牌。
 * <p>WebFlux 环境下 OAuth2 Resource Server 自动配置通常只注册 {@code ReactiveJwtDecoder}，
 * 导致 {@code ObjectProvider<JwtDecoder>} 为空，OAuth2 登录令牌无法通过 {@code PlatformApiAuthenticationWebFilter}。</p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri")
public class GatewayOAuth2JwtConfiguration {

    /**
     * 执行网关JwtDecoder。
     * @return 执行结果
     */
    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder gatewayJwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri) {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}
