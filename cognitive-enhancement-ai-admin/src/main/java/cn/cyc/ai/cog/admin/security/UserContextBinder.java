package cn.cyc.ai.cog.admin.security;

import cn.cyc.ai.cog.admin.config.AdminAuthProperties;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 将 Bearer / 网关头 / 开发占位解析为 {@link UserContext}。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class UserContextBinder {

    /** 管理后台认证Properties。 */
    private final AdminAuthProperties adminAuthProperties;
    /** bearerIdentity服务。 */
    private final AdminBearerIdentityService bearerIdentityService;
    /** 路径Matcher。 */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 创建UserContextBinder。
     */
    public UserContextBinder(AdminAuthProperties adminAuthProperties,
                             ObjectProvider<AdminBearerIdentityService> bearerIdentityService) {
        this.adminAuthProperties = adminAuthProperties;
        this.bearerIdentityService = bearerIdentityService.getIfAvailable();
    }

    /**
     * 执行supports。
     *
     * @param request 请求
     * @return 执行结果
     */
    public boolean supports(HttpServletRequest request) {
        String path = request.getRequestURI();
        return pathMatcher.match("/api/admin/**", path)
                || pathMatcher.match("/api/runtime/**", path);
    }

    /**
     * 执行bindIfNeeded。
     *
     * @param request 请求
     */
    public void bindIfNeeded(HttpServletRequest request) {
        if (!supports(request)) {
            return;
        }
        String authorization = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (bearerIdentityService != null && StringUtils.hasText(authorization)) {
            AuthUser bearerUser = bearerIdentityService.parseBearer(authorization);
            if (bearerUser != null) {
                bindUser(bearerUser);
                return;
            }
        }
        String userId = request.getHeader(SecurityConstants.HEADER_USER_ID);
        if (adminAuthProperties.isTrustGatewayHeaders() && StringUtils.hasText(userId)) {
            bindFromGatewayHeaders(request);
            return;
        }
        if (adminAuthProperties.isDevAuthBypass()) {
            bindUser(devAdminUser());
        }
    }

    /**
     * 执行clear。
     */
    public void clear() {
        UserContext.clear();
        TenantContext.clear();
    }

    /**
     * 执行bindFrom网关Headers。
     *
     * @param request 请求
     */
    private void bindFromGatewayHeaders(HttpServletRequest request) {
        AuthUser user = new AuthUser();
        user.setUserId(parseLong(request.getHeader(SecurityConstants.HEADER_USER_ID)));
        user.setUsername(request.getHeader(SecurityConstants.HEADER_USERNAME));
        user.setTenantCode(request.getHeader(SecurityConstants.HEADER_TENANT));
        String tenantIdHeader = request.getHeader(SecurityConstants.HEADER_TENANT_ID);
        if (StringUtils.hasText(tenantIdHeader)) {
            user.setTenantId(parseLong(tenantIdHeader));
        }
        user.setRoles(splitHeader(request.getHeader(SecurityConstants.HEADER_ROLES)));
        user.setAuthorities(splitHeader(request.getHeader(SecurityConstants.HEADER_AUTHORITIES)));
        bindUser(user);
    }

    /**
     * 执行dev管理后台用户。
     * @return 执行结果
     */
    private AuthUser devAdminUser() {
        AuthUser devAdmin = new AuthUser(1L, "admin", CommonConstants.DEFAULT_TENANT,
                List.of("ADMIN"), List.of("admin:role:update", "admin:role:create",
                "admin:permission:create", "admin:order:update", "admin:order:refund",
                "admin:member:update", "admin:member:grant", "admin:content:update",
                "admin:content:audit", "admin:banner:create", "admin:banner:update",
                "admin:dict:read", "admin:dict:update", "admin:user:view", "admin:user:update"));
        devAdmin.setTenantId(CommonConstants.DEFAULT_TENANT_ID);
        return devAdmin;
    }

    /**
     * 执行bind用户。
     *
     * @param user 用户
     */
    private void bindUser(AuthUser user) {
        UserContext.set(user);
        if (StringUtils.hasText(user.getTenantCode())) {
            TenantContext.setTenantCode(user.getTenantCode());
        }
        if (user.getTenantId() != null) {
            TenantContext.setTenantId(user.getTenantId());
        } else if (StringUtils.hasText(user.getTenantCode())) {
            TenantContext.setTenantCode(user.getTenantCode());
        }
    }

    /**
     * 执行parseLong。
     *
     * @param value 值
     * @return 执行结果
     */
    private Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 执行splitHeader。
     *
     * @param header header
     * @return 执行结果
     */
    private List<String> splitHeader(String header) {
        if (!StringUtils.hasText(header)) {
            return Collections.emptyList();
        }
        return Arrays.stream(header.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }
}
