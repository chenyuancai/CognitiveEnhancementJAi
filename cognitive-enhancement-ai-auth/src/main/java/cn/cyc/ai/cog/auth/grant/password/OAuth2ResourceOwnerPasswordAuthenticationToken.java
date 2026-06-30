package cn.cyc.ai.cog.auth.grant.password;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 资源所有者密码模式认证令牌。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class OAuth2ResourceOwnerPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    /** password 授权类型常量（OAuth2.1 已弃用标准定义，此处自定义实现）。 */
    public static final AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password");

    /** scopes。 */
    private final Set<String> scopes;

    /**
     * 创建OAuth2ResourceOwnerPasswordAuthenticationToken。
     */
    public OAuth2ResourceOwnerPasswordAuthenticationToken(Authentication clientPrincipal,
                                                          @Nullable Set<String> scopes,
                                                          @Nullable Map<String, Object> additionalParameters) {
        super(PASSWORD, clientPrincipal, additionalParameters);
        this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
    }

    /**
     * 获取Scopes。
     * @return Scopes
     */
    public Set<String> getScopes() {
        return scopes;
    }
}
