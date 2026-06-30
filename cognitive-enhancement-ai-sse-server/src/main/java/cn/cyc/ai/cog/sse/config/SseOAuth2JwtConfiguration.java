package cn.cyc.ai.cog.sse.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * SSE 侧 OAuth2 JWT 解码（仅用于 Bearer 解析，不启用全局 Resource Server 过滤器）。
 */
@Configuration
@ConditionalOnProperty(name = "cog.sse.oauth2-jwk-set-uri")
public class SseOAuth2JwtConfiguration {

    @Bean
    public JwtDecoder sseJwtDecoder(SseProperties sseProperties) {
        return NimbusJwtDecoder.withJwkSetUri(sseProperties.getOauth2JwkSetUri()).build();
    }
}
