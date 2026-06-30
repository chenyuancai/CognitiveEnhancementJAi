package cn.cyc.ai.cog.center.user;

import cn.cyc.ai.cog.api.enums.ErrorCode;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.jwt.CompositeBearerIdentityParser;
import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import cn.cyc.ai.cog.runtime.security.JwtProperties;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT 认证过滤器：OAuth2 RS256 / legacy HS256 Bearer，或信任网关透传身份头。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** jwtProperties。 */
    private final JwtProperties jwtProperties;
    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;
    /** bearerIdentityParser。 */
    private final CompositeBearerIdentityParser bearerIdentityParser;
    /** 路径Matcher。 */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 创建JwtAuthentication过滤器。
     */
    public JwtAuthenticationFilter(JwtProperties jwtProperties,
                                   ObjectMapper objectMapper,
                                   @Qualifier("adminJwtDecoder") ObjectProvider<JwtDecoder> adminJwtDecoderProvider,
                                   @Qualifier("appJwtDecoder") ObjectProvider<JwtDecoder> appJwtDecoderProvider) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
        JwtDecoder jwtDecoder = adminJwtDecoderProvider.getIfAvailable();
        if (jwtDecoder == null) {
            jwtDecoder = appJwtDecoderProvider.getIfAvailable();
        }
        this.bearerIdentityParser = new CompositeBearerIdentityParser(
                jwtProperties.getSecret(), jwtDecoder);
    }

    /**
     * 执行do过滤器Internal。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!jwtProperties.isAuthEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        if (isPermitAll(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtProperties.isTrustGatewayHeaders()
                && StringUtils.hasText(request.getHeader(SecurityConstants.HEADER_USER_ID))) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        AuthUser user = bearerIdentityParser.parseBearer(authorization);
        if (user == null) {
            writeUnauthorized(response, "未认证或认证已失效");
            return;
        }

        request.setAttribute(AuthSupport.ATTR_USER_ID, user.getUserId());
        request.setAttribute(AuthSupport.ATTR_USERNAME, user.getUsername());
        request.setAttribute(AuthSupport.ATTR_TENANT_CODE, user.getTenantCode());
        request.setAttribute(AuthSupport.ATTR_ROLES, user.getRoles());
        String tenantCode = user.getTenantCode();
        try {
            if (StringUtils.hasText(tenantCode)) {
                TenantContext.setTenantCode(tenantCode);
            }
            if (user.getTenantId() != null) {
                TenantContext.setTenantId(user.getTenantId());
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * 判断是否为PermitAll。
     *
     * @param path 路径
     * @return 是否满足条件
     */
    private boolean isPermitAll(String path) {
        List<String> permitAll = jwtProperties.getPermitAll();
        if (permitAll == null || permitAll.isEmpty()) {
            return false;
        }
        return permitAll.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 执行writeUnauthorized。
     *
     * @param response 响应
     * @param message 消息
     */
    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(ErrorCode.UNAUTHORIZED.getHttpStatus());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse<Void> body = ApiResponse.failure(ErrorCode.UNAUTHORIZED, message, null);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
