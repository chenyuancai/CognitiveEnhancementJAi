package cn.cyc.ai.cog.common.jwt;

import cn.cyc.ai.cog.common.context.AuthUser;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.StringUtils;

/**
 * 复合 Bearer 解析：优先 OAuth2 RS256，回退 legacy HS256。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class CompositeBearerIdentityParser {

    /** legacyParser。 */
    private final LegacyHs256BearerParser legacyParser;
    /** fixedJwtDecoder。 */
    private final JwtDecoder fixedJwtDecoder;
    /** jwtDecoder提供者。 */
    private final ObjectProvider<JwtDecoder> jwtDecoderProvider;

    /**
     * 创建CompositeBearerIdentityParser。
     *
     * @param legacyJwtSecret legacyJwtSecret
     * @param jwtDecoder jwtDecoder
     */
    public CompositeBearerIdentityParser(String legacyJwtSecret, JwtDecoder jwtDecoder) {
        this.legacyParser = new LegacyHs256BearerParser(legacyJwtSecret);
        this.fixedJwtDecoder = jwtDecoder;
        this.jwtDecoderProvider = null;
    }

    /** 从 Spring 容器按需解析 JwtDecoder，避免构造期固定 Bean 导致多 Primary 冲突。 */
    public CompositeBearerIdentityParser(String legacyJwtSecret, ObjectProvider<JwtDecoder> jwtDecoderProvider) {
        this.legacyParser = new LegacyHs256BearerParser(legacyJwtSecret);
        this.fixedJwtDecoder = null;
        this.jwtDecoderProvider = jwtDecoderProvider;
    }

    /** 从 Authorization: Bearer 解析用户上下文；无效时返回 null。 */
    public AuthUser parseBearer(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)
                || !authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            return null;
        }
        String token = authorizationHeader.substring(SecurityConstants.BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            return null;
        }
        JwtDecoder jwtDecoder = fixedJwtDecoder != null
                ? fixedJwtDecoder
                : (jwtDecoderProvider != null ? jwtDecoderProvider.getIfAvailable() : null);
        if (jwtDecoder != null) {
            try {
                Jwt jwt = jwtDecoder.decode(token);
                return OAuth2JwtAuthUserMapper.fromJwt(jwt);
            } catch (JwtException ignored) {
                // 尝试 legacy HS256
            }
        }
        return legacyParser.parseBearer(authorizationHeader);
    }
}
