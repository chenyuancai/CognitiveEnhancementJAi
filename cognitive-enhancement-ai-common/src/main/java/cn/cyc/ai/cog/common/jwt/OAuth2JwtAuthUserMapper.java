package cn.cyc.ai.cog.common.jwt;

import cn.cyc.ai.cog.common.context.AuthUser;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

/**
 * OAuth2 JWT 声明 → {@link AuthUser} 映射。
 */
public final class OAuth2JwtAuthUserMapper {

    private OAuth2JwtAuthUserMapper() {
    }

    public static AuthUser fromJwt(Jwt jwt) {
        AuthUser user = new AuthUser(
                parseLong(claimAsString(jwt, SecurityConstants.CLAIM_USER_ID)),
                claimAsString(jwt, SecurityConstants.CLAIM_USERNAME),
                claimAsString(jwt, SecurityConstants.CLAIM_TENANT),
                claimAsCsv(jwt, SecurityConstants.CLAIM_ROLES),
                claimAsCsv(jwt, SecurityConstants.CLAIM_AUTHORITIES));
        user.setTenantId(parseLong(claimAsString(jwt, SecurityConstants.CLAIM_TENANT_ID)));
        return user;
    }

    private static String claimAsString(Jwt jwt, String name) {
        Object value = jwt.getClaim(name);
        return value == null ? "" : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private static List<String> claimAsCsv(Jwt jwt, String name) {
        Object value = jwt.getClaim(name);
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return value == null ? List.of() : List.of(String.valueOf(value));
    }

    private static Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
