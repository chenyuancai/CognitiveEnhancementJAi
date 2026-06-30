package cn.cyc.ai.cog.sse.security;

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
 * SSE 客户端 API 用户上下文绑定过滤器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 40)
public class SseUserContextBindingFilter extends OncePerRequestFilter {

    /** sse用户上下文Binder。 */
    private final SseUserContextBinder sseUserContextBinder;

    /**
     * 创建Sse用户上下文Binding过滤器。
     *
     * @param sseUserContextBinder sse用户上下文Binder
     */
    public SseUserContextBindingFilter(SseUserContextBinder sseUserContextBinder) {
        this.sseUserContextBinder = sseUserContextBinder;
    }

    /**
     * 执行do过滤器Internal。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        boolean supported = sseUserContextBinder.supports(request);
        try {
            if (supported) {
                sseUserContextBinder.bindIfNeeded(request);
            }
            filterChain.doFilter(request, response);
        } finally {
            if (supported) {
                sseUserContextBinder.clear();
            }
        }
    }
}
