package cn.cyc.ai.cog.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * App 侧 OAuth2 JWT 解码配置。
 * <p>
 * 不启用全局 Resource Server 过滤器，仅提供 {@link JwtDecoder} Bean 供 Bearer 解析使用。
 * </p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@ConditionalOnProperty(name = "cog.app.oauth2-jwk-set-uri")
public class AppOAuth2JwtConfiguration {

    /**
     * 基于 JWK Set URI 构建 JWT 解码器。
     *
     * @param appAuthProperties C 端鉴权配置
     * @return JWT 解码器
     */
    @Bean
    public JwtDecoder appJwtDecoder(AppAuthProperties appAuthProperties) {
        return NimbusJwtDecoder.withJwkSetUri(appAuthProperties.getOauth2JwkSetUri()).build();
    }
}
