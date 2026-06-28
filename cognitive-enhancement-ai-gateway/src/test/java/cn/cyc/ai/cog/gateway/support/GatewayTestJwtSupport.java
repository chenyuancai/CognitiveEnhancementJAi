package cn.cyc.ai.cog.gateway.support;

import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * 网关集成测试用 legacy HS256 JWT。
 */
public final class GatewayTestJwtSupport {

    private static final String DEFAULT_SECRET = "cognitive-enhancement-ai-default-secret-key";

    private GatewayTestJwtSupport() {
    }

    public static String legacyBearerToken() {
        return legacyBearerToken(1L, "admin", "default", List.of("ADMIN"));
    }

    public static String legacyBearerToken(Long userId, String username, String tenantCode, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(DEFAULT_SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("tenantCode", tenantCode)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000L))
                .signWith(key)
                .compact();
        return SecurityConstants.BEARER_PREFIX + token;
    }
}
