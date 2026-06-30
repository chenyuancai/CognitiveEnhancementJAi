package cn.cyc.ai.cog.admin.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证上下文拦截器：委托 {@link UserContextBinder}（Filter 已绑定，此处保持兼容二次进入）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AuthContextInterceptor implements HandlerInterceptor {

    /** 用户上下文Binder。 */
    private final UserContextBinder userContextBinder;

    /**
     * 创建认证上下文拦截器。
     *
     * @param userContextBinder 用户上下文Binder
     */
    public AuthContextInterceptor(UserContextBinder userContextBinder) {
        this.userContextBinder = userContextBinder;
    }

    /**
     * 执行preHandle。
     *
     * @param request 请求
     * @param response 响应
     * @param handler 处理器
     * @return 执行结果
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (userContextBinder.supports(request) && cn.cyc.ai.cog.common.context.UserContext.get() == null) {
            userContextBinder.bindIfNeeded(request);
        }
        return true;
    }

    /**
     * 执行afterCompletion。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 清理由 UserContextBindingFilter 负责
    }
}
