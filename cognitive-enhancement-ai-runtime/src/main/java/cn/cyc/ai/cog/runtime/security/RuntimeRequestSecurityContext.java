package cn.cyc.ai.cog.runtime.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * 从当前 HTTP 请求读取 JWT 认证上下文，供 Runtime 策略治理使用。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class RuntimeRequestSecurityContext {

    /**
     * 与 {@code AuthSupport.ATTR_ROLES} 保持一致。
     */
    public static final String ATTR_ROLES = "roles";

    /** jwtProperties。 */
    private final JwtProperties jwtProperties;

    /**
     * 创建RuntimeRequestSecurityContext。
     *
     * @param jwtProperties jwtProperties
     */
    public RuntimeRequestSecurityContext(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * JWT 鉴权是否启用。
     *
     * @return 是否启用
     */
    public boolean isAuthEnabled() {
        return jwtProperties.isAuthEnabled();
    }

    /**
     * 当前请求持有的角色列表。
     *
     * @return 角色列表；无请求上下文时返回空列表
     */
    @SuppressWarnings("unchecked")
    public List<String> currentRoles() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return List.of();
        }
        Object roles = request.getAttribute(ATTR_ROLES);
        if (roles instanceof List<?> list) {
            return (List<String>) list;
        }
        return Collections.emptyList();
    }

    /**
     * 判断当前请求是否持有指定角色。
     *
     * @param role 角色名
     * @return 是否持有
     */
    public boolean hasRole(String role) {
        if (role == null || role.isBlank()) {
            return true;
        }
        return currentRoles().contains(role);
    }

    /**
     * 执行current请求。
     * @return 执行结果
     */
    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}
