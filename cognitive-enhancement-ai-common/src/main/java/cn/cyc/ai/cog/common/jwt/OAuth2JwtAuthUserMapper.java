package cn.cyc.ai.cog.common.jwt;

import cn.cyc.ai.cog.common.context.AuthUser;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

/**
 * OAuth2 JWT 声明 → {@link AuthUser} 映射。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class OAuth2JwtAuthUserMapper {

    /**
     * 创建OAuth2Jwt认证用户数据访问 Mapper。
     */
    private OAuth2JwtAuthUserMapper() {
    }

    /**
     * 执行fromJwt。
     *
     * @param jwt jwt
     * @return 执行结果
     */
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

    /**
     * 执行claimAsString。
     *
     * @param jwt jwt
     * @param name 名称
     * @return 执行结果
     */
    private static String claimAsString(Jwt jwt, String name) {
        Object value = jwt.getClaim(name);
        return value == null ? "" : String.valueOf(value);
    }

    /**
     * 执行claimAsCsv。
     *
     * @param jwt jwt
     * @param name 名称
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    private static List<String> claimAsCsv(Jwt jwt, String name) {
        Object value = jwt.getClaim(name);
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return value == null ? List.of() : List.of(String.valueOf(value));
    }

    /**
     * 执行parseLong。
     *
     * @param value 值
     * @return 执行结果
     */
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
