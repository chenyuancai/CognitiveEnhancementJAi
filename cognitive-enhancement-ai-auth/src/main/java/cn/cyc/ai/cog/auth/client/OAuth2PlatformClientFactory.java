package cn.cyc.ai.cog.auth.client;

import cn.cyc.ai.cog.auth.grant.password.OAuth2ResourceOwnerPasswordAuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;

/**
 * 平台内置 OAuth2 客户端定义（cms-client）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class OAuth2PlatformClientFactory {

    /** 固定 RegisteredClient 主键，便于 JDBC 幂等种子。 */
    public static final String CMS_CLIENT_REGISTRATION_ID = "8f3c2a1b-6d5e-4f7a-9b0c-1d2e3f4a5b6c";

    /** CMS客户端ID */
    public static final String CMS_CLIENT_ID = "cms-client";

    /**
     * 创建OAuth2PlatformClientFactory。
     */
    private OAuth2PlatformClientFactory() {
    }

    /**
     * 构建Cms客户端。
     * @return 构建结果
     */
    public static RegisteredClient buildCmsClient() {
        return RegisteredClient.withId(CMS_CLIENT_REGISTRATION_ID)
                .clientId(CMS_CLIENT_ID)
                .clientSecret("{noop}cms-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(OAuth2ResourceOwnerPasswordAuthenticationToken.PASSWORD)
                .redirectUri("http://127.0.0.1:5173/cms/login/callback")
                .postLogoutRedirectUri("http://127.0.0.1:5173/cms/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("cms.read")
                .scope("cms.write")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(2))
                        .refreshTokenTimeToLive(Duration.ofDays(7))
                        .reuseRefreshTokens(false)
                        .build())
                .build();
    }
}
