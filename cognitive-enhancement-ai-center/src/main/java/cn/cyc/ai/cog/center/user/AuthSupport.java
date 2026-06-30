package cn.cyc.ai.cog.center.user;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.runtime.security.JwtProperties;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 请求级鉴权辅助，配合 JWT Filter 使用。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AuthSupport {

    /** ATTR用户ID */
    public static final String ATTR_USER_ID = "userId";
    /** ATTRUSERNAME。 */
    public static final String ATTR_USERNAME = "username";
    /** ATTR租户编码。 */
    public static final String ATTR_TENANT_CODE = "tenantCode";
    /** ATTRROLES。 */
    public static final String ATTR_ROLES = "roles";

    /** jwtProperties。 */
    private final JwtProperties jwtProperties;

    /**
     * 创建认证支持工具。
     *
     * @param jwtProperties jwtProperties
     */
    public AuthSupport(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 要求当前请求已认证。
     */
    public void requireAuthenticated(HttpServletRequest request) {
        if (!jwtProperties.isAuthEnabled()) {
            return;
        }
        if (request.getAttribute(ATTR_USER_ID) == null) {
            throw Errors.of(PlatformErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * 要求当前请求持有指定角色。
     */
    public void requireRole(HttpServletRequest request, String role) {
        if (!jwtProperties.isAuthEnabled()) {
            return;
        }
        requireAuthenticated(request);
        List<String> roles = extractRoles(request);
        if (roles == null || !roles.contains(role)) {
            throw Errors.of(PlatformErrorCode.FORBIDDEN);
        }
    }

    /**
     * 执行extractRoles。
     *
     * @param request 请求
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(HttpServletRequest request) {
        Object roles = request.getAttribute(ATTR_ROLES);
        if (roles instanceof List<?> list) {
            return (List<String>) list;
        }
        return null;
    }

    /**
     * 读取当前请求租户，未认证或未设置时回退到 default。
     */
    public String currentTenantCode(HttpServletRequest request) {
        Object tenantCode = request.getAttribute(ATTR_TENANT_CODE);
        return tenantCode instanceof String value ? TenantContext.normalize(value) : TenantContext.DEFAULT_TENANT_CODE;
    }
}
