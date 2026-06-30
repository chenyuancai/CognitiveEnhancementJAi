package cn.cyc.ai.cog.app.security;

import cn.cyc.ai.cog.app.config.AppAuthProperties;
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
  * AppUserContextBinder
  *
  * @author cyc
  * @date 2026/6/15 14:18
  */
@Component
public class AppUserContextBinder {

    /** C 端鉴权配置 */
    private final AppAuthProperties appAuthProperties;

    /** Bearer Token 解析服务（OAuth2 未配置时可为 null） */
    private final AppBearerIdentityService bearerIdentityService;

    /** 路径匹配器 */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 创建AppUserContextBinder。
     */
    public AppUserContextBinder(AppAuthProperties appAuthProperties,
                                ObjectProvider<AppBearerIdentityService> bearerIdentityService) {
        this.appAuthProperties = appAuthProperties;
        this.bearerIdentityService = bearerIdentityService.getIfAvailable();
    }

    /**
     * 执行supports。
     *
     * @param request 请求
     * @return 执行结果
     */
    /**
     * 判断是否需绑定用户上下文（C 端业务与练习 API）。
     *
     * @param request 请求
     * @return 是否支持绑定
     */
    public boolean supports(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // /api/practice/** 与 /api/app/** 均需用户上下文（练习域独立路径前缀）
        return pathMatcher.match("/api/app/**", uri) || pathMatcher.match("/api/practice/**", uri);
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
        if (appAuthProperties.isTrustGatewayHeaders() && StringUtils.hasText(userId)) {
            bindFromGatewayHeaders(request);
            return;
        }
        if (appAuthProperties.isDevAuthBypass()) {
            bindUser(devAppUser());
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
     * 执行devC端用户。
     * @return 执行结果
     */
    private AuthUser devAppUser() {
        AuthUser devUser = new AuthUser(1L, "admin", CommonConstants.DEFAULT_TENANT,
                List.of("USER"), List.of());
        devUser.setTenantId(CommonConstants.DEFAULT_TENANT_ID);
        return devUser;
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
