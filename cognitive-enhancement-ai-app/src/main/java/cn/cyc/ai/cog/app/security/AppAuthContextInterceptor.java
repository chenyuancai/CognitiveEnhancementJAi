package cn.cyc.ai.cog.app.security;

import cn.cyc.ai.cog.common.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * C 端鉴权上下文拦截器：在 Filter 未绑定用户时二次尝试绑定。
 */
@Component
public class AppAuthContextInterceptor implements HandlerInterceptor {

    /** 用户上下文绑定器 */
    private final AppUserContextBinder appUserContextBinder;

    /**
     * @param appUserContextBinder 用户上下文绑定器
     */
    public AppAuthContextInterceptor(AppUserContextBinder appUserContextBinder) {
        this.appUserContextBinder = appUserContextBinder;
    }

    /**
     * 请求进入 Controller 前补绑用户上下文。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @return 是否继续处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (appUserContextBinder.supports(request) && UserContext.get() == null) {
            appUserContextBinder.bindIfNeeded(request);
        }
        return true;
    }
}
