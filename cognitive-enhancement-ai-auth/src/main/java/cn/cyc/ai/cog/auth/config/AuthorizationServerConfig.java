package cn.cyc.ai.cog.auth.config;

import cn.cyc.ai.cog.auth.grant.password.OAuth2ResourceOwnerPasswordAuthenticationConverter;
import cn.cyc.ai.cog.auth.grant.password.OAuth2ResourceOwnerPasswordAuthenticationProvider;
import cn.cyc.ai.cog.auth.grant.password.OAuth2ResourceOwnerPasswordAuthenticationToken;
import cn.cyc.ai.cog.auth.user.AuthUserDetails;
import cn.cyc.ai.cog.auth.user.DbUserDetailsService;
import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.http.MediaType;

/**
 * OAuth2 授权服务核心配置：协议端点、客户端、JWK、JWT 声明定制与令牌生成器。
 *
 * <p>支持 authorization_code / refresh_token / client_credentials / password 四种授权类型，
 * 其中 password 通过自定义 Converter + Provider 实现。</p>
 *
 * @author cyc
 */
@Configuration
@EnableConfigurationProperties(AuthJwkProperties.class)
public class AuthorizationServerConfig {

    /** 授权服务协议端点过滤链（最高优先级），并装配 password 自定义授权。 */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager,
            OAuth2AuthorizationService authorizationService,
            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        OAuth2ResourceOwnerPasswordAuthenticationProvider passwordProvider =
                new OAuth2ResourceOwnerPasswordAuthenticationProvider(
                        authenticationManager, authorizationService, tokenGenerator);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults())
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                        .accessTokenRequestConverter(new OAuth2ResourceOwnerPasswordAuthenticationConverter())
                        .authenticationProvider(passwordProvider));

        http.exceptionHandling(e -> e.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"),
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));
        return http.build();
    }

    /** 账号密码认证管理器（基于数据库用户）。 */
    @Bean
    public AuthenticationManager authenticationManager(DbUserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    /** 令牌生成器：JWT（含自定义声明）+ 访问令牌 + 刷新令牌。 */
    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator(
            JwtEncoder jwtEncoder, OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(jwtCustomizer);
        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator, new OAuth2AccessTokenGenerator(), new OAuth2RefreshTokenGenerator());
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    /** 向 JWT 写入平台自定义声明（userId/tenantCode/roles/authorities）。 */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            if (context.getPrincipal().getPrincipal() instanceof AuthUserDetails user) {
                context.getClaims().claim(SecurityConstants.CLAIM_USER_ID, user.getUserId());
                context.getClaims().claim(SecurityConstants.CLAIM_USERNAME, user.getUsername());
                context.getClaims().claim(SecurityConstants.CLAIM_TENANT, user.getTenantCode());
                if (user.getTenantId() != null) {
                    context.getClaims().claim(SecurityConstants.CLAIM_TENANT_ID, user.getTenantId());
                }
                context.getClaims().claim(SecurityConstants.CLAIM_ROLES, user.getRoles());
                context.getClaims().claim(SecurityConstants.CLAIM_AUTHORITIES, user.getPermissions());
            }
        };
    }

    /** RSA 签名密钥源（持久化到本地文件；生产可替换为 KMS）。 */
    @Bean
    public JWKSource<SecurityContext> jwkSource(PersistentRsaJwkSupport jwkSupport,
                                                  AuthJwkProperties jwkProperties) {
        return jwkSupport.loadOrCreate(jwkProperties);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(
            @org.springframework.beans.factory.annotation.Value("${cog.auth.issuer:http://localhost:8802}") String issuer) {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .build();
    }
}
