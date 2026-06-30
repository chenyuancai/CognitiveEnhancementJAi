package cn.cyc.ai.cog.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 /**
  * <p>
  * 请求结束时清理 {@link cn.cyc.ai.cog.common.context.UserContext}，避免线程复用泄漏。
  * </p>
  *
  * @author cyc
  * @date 2026/6/15 14:18
  */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 40)
public class AppUserContextBindingFilter extends OncePerRequestFilter {

    /** 用户上下文绑定器 */
    private final AppUserContextBinder appUserContextBinder;

    /**
     * @param appUserContextBinder 用户上下文绑定器
     */
    public AppUserContextBindingFilter(AppUserContextBinder appUserContextBinder) {
        this.appUserContextBinder = appUserContextBinder;
    }

    /**
     * 绑定用户上下文并在 finally 中清理。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        boolean supported = appUserContextBinder.supports(request);
        try {
            if (supported) {
                appUserContextBinder.bindIfNeeded(request);
            }
            filterChain.doFilter(request, response);
        } finally {
            if (supported) {
                appUserContextBinder.clear();
            }
        }
    }
}
