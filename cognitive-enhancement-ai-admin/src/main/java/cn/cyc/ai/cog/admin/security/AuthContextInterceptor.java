package cn.cyc.ai.cog.admin.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证上下文拦截器：委托 {@link UserContextBinder}（Filter 已绑定，此处保持兼容二次进入）。
 */
@Component
public class AuthContextInterceptor implements HandlerInterceptor {

    private final UserContextBinder userContextBinder;

    public AuthContextInterceptor(UserContextBinder userContextBinder) {
        this.userContextBinder = userContextBinder;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (userContextBinder.supports(request) && cn.cyc.ai.cog.common.context.UserContext.get() == null) {
            userContextBinder.bindIfNeeded(request);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 清理由 UserContextBindingFilter 负责
    }
}
