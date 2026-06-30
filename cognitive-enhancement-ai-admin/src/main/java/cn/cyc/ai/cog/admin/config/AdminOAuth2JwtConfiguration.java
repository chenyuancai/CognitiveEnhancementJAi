package cn.cyc.ai.cog.admin.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Admin 侧 OAuth2 JWT 解码（不启用全局 Resource Server 过滤器，仅提供 JwtDecoder Bean）。
 * <p>
 * 仅当 {@code cog.admin.oauth2-jwk-set-uri} 配置为非空 URL 时注册；网关透传头模式可不配置。
 * </p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText('${cog.admin.oauth2-jwk-set-uri:}')")
public class AdminOAuth2JwtConfiguration {

    /**
     * 执行管理后台JwtDecoder。
     *
     * @param adminAuthProperties 管理后台认证Properties
     * @return 执行结果
     */
    @Bean
    public JwtDecoder adminJwtDecoder(AdminAuthProperties adminAuthProperties) {
        return NimbusJwtDecoder.withJwkSetUri(adminAuthProperties.getOauth2JwkSetUri()).build();
    }
}
