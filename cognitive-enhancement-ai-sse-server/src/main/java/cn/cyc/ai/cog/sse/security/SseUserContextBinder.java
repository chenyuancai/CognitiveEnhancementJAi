package cn.cyc.ai.cog.sse.security;

import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import cn.cyc.ai.cog.sse.config.SseProperties;
import cn.cyc.ai.cog.sse.security.SseBearerIdentityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 将网关透传头解析为 {@link UserContext}（仅客户端连接 API）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class SseUserContextBinder {

    /** sseProperties。 */
    private final SseProperties sseProperties;

    /** Bearer 身份解析。 */
    private final SseBearerIdentityService sseBearerIdentityService;

    /** 路径Matcher。 */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 创建SseUserContextBinder。
     */
    public SseUserContextBinder(SseProperties sseProperties,
                                SseBearerIdentityService sseBearerIdentityService) {
        this.sseProperties = sseProperties;
        this.sseBearerIdentityService = sseBearerIdentityService;
    }

    /**
     * 执行supports。
     *
     * @param request 请求
     * @return 执行结果
     */
    public boolean supports(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return pathMatcher.match("/api/sse/connect", uri)
                || pathMatcher.match("/api/sse/disconnect", uri);
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
        AuthUser bearerUser = sseBearerIdentityService.parseBearer(authorization);
        if (bearerUser != null) {
            bindUser(bearerUser);
            return;
        }
        String userId = request.getHeader(SecurityConstants.HEADER_USER_ID);
        if (sseProperties.isTrustGatewayHeaders() && StringUtils.hasText(userId)) {
            bindFromGatewayHeaders(request);
            return;
        }
        if (sseProperties.isDevAuthBypass()) {
            bindUser(devUser());
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
     * 执行dev用户。
     * @return 执行结果
     */
    private AuthUser devUser() {
        AuthUser user = new AuthUser(1L, "admin", CommonConstants.DEFAULT_TENANT, List.of("USER"), List.of());
        user.setTenantId(CommonConstants.DEFAULT_TENANT_ID);
        return user;
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
        } catch (NumberFormatException exception) {
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
