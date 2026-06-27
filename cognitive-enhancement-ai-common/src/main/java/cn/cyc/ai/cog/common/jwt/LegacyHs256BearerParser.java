package cn.cyc.ai.cog.common.jwt;

import cn.cyc.ai.cog.common.context.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * 解析 legacy HS256 Bearer JWT（与 runtime {@code JwtUtil} 对齐）。
 */
public class LegacyHs256BearerParser {

    private final SecretKey key;

    public LegacyHs256BearerParser(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 从 Authorization 头解析；无效时返回 null。 */
    public AuthUser parseBearer(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)
                || !authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            return null;
        }
        String token = authorizationHeader.substring(SecurityConstants.BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Long userId = Long.valueOf(claims.getSubject());
            String username = claims.get("username", String.class);
            String tenantCode = claims.get("tenantCode", String.class);
            List<String> roles = readStringList(claims.get("roles"));
            return new AuthUser(userId, username, tenantCode, roles, Collections.emptyList());
        } catch (Exception ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> readStringList(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return Collections.emptyList();
    }
}
