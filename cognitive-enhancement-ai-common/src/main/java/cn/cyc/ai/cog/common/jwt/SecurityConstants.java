package cn.cyc.ai.cog.common.jwt;

/**
 * 认证与令牌相关常量（借鉴 zcloud-core-jwt / zcloud-core-secure）。
 *
 * <p>网关与各资源服务通过这些约定从 OAuth2 JWT 中解析用户上下文，并在内部请求头透传。</p>
 *
 * @author cyc
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }

    /** 标准鉴权请求头。 */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** Bearer 令牌前缀。 */
    public static final String BEARER_PREFIX = "Bearer ";

    /** 网关解析后向下游透传的用户 ID 头。 */
    public static final String HEADER_USER_ID = "X-User-Id";

    /** 网关解析后向下游透传的用户名头。 */
    public static final String HEADER_USERNAME = "X-Username";

    /** 网关解析后向下游透传的租户头。 */
    public static final String HEADER_TENANT = "X-Tenant-Code";

    /** 网关解析后向下游透传的租户 ID 头。 */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    /** 网关解析后向下游透传的角色头（逗号分隔）。 */
    public static final String HEADER_ROLES = "X-Roles";

    /** 网关解析后向下游透传的权限点头（逗号分隔）。 */
    public static final String HEADER_AUTHORITIES = "X-Authorities";

    // ---- JWT 自定义声明名 ----

    /** 用户 ID 声明。 */
    public static final String CLAIM_USER_ID = "userId";

    /** 用户名声明。 */
    public static final String CLAIM_USERNAME = "username";

    /** 租户编码声明。 */
    public static final String CLAIM_TENANT = "tenantCode";

    /** 租户 ID 声明。 */
    public static final String CLAIM_TENANT_ID = "tenantId";

    /** 角色列表声明。 */
    public static final String CLAIM_ROLES = "roles";

    /** 权限点列表声明。 */
    public static final String CLAIM_AUTHORITIES = "authorities";

    /** 网关已验签用户上下文（WebFlux exchange attribute）。 */
    public static final String GATEWAY_AUTH_USER_ATTR = "cog.gateway.authUser";
}
