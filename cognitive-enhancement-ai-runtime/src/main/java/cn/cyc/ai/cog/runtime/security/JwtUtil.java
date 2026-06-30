package cn.cyc.ai.cog.runtime.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT 工具类，负责 token 生成与解析。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class JwtUtil {

    /** properties。 */
    private final JwtProperties properties;
    /** 键。 */
    private final SecretKey key;

    /**
     * 创建JwtUtil。
     *
     * @param properties properties
     */
    public JwtUtil(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT token。
     */
    public String generateToken(Long userId, String username, List<String> roles) {
        return generateToken(userId, username, TenantContext.DEFAULT_TENANT_CODE, roles);
    }

    /**
     * 生成带租户信息的 JWT token。
     */
    public String generateToken(Long userId, String username, String tenantCode, List<String> roles) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("tenantCode", TenantContext.normalize(tenantCode))
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + properties.getExpirationMs()))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 token 获取 Claims。
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 token 是否有效。
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 token 中提取用户 ID。
     */
    public Long extractUserId(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }

    /**
     * 从 token 中提取用户名。
     */
    @SuppressWarnings("unchecked")
    public String extractUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 从 token 中提取租户编码。
     */
    public String extractTenantCode(String token) {
        return TenantContext.normalize(parseToken(token).get("tenantCode", String.class));
    }

    /**
     * 从 token 中提取角色列表。
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return parseToken(token).get("roles", List.class);
    }
}
