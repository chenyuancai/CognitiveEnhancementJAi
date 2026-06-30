package cn.cyc.ai.cog.admin.security;

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
 * 在 Filter 链早期绑定 {@link cn.cyc.ai.cog.common.context.UserContext}，覆盖 Admin/App/Runtime API。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 40)
public class UserContextBindingFilter extends OncePerRequestFilter {

    /** 用户上下文Binder。 */
    private final UserContextBinder userContextBinder;

    /**
     * 创建用户上下文Binding过滤器。
     *
     * @param userContextBinder 用户上下文Binder
     */
    public UserContextBindingFilter(UserContextBinder userContextBinder) {
        this.userContextBinder = userContextBinder;
    }

    /**
     * 执行do过滤器Internal。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            userContextBinder.bindIfNeeded(request);
            filterChain.doFilter(request, response);
        } finally {
            userContextBinder.clear();
        }
    }
}
