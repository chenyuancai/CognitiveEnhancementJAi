package cn.cyc.ai.cog.app.security;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.app.config.AppRateLimitProperties;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * C 端 API 滑动窗口限流拦截器。
 */
@Component
public class AppRateLimitInterceptor implements HandlerInterceptor {

    private final AppRateLimitProperties properties;
    private final Map<String, Deque<Long>> windows = new ConcurrentHashMap<>();

    /**
     * @param properties 限流配置
     */
    public AppRateLimitInterceptor(AppRateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!properties.isEnabled()) {
            return true;
        }
        String clientKey = resolveClientKey(request);
        long now = System.currentTimeMillis();
        long windowStart = now - 60_000L;
        Deque<Long> deque = windows.computeIfAbsent(clientKey, key -> new ArrayDeque<>());
        synchronized (deque) {
            while (!deque.isEmpty() && deque.peekFirst() < windowStart) {
                deque.pollFirst();
            }
            if (deque.size() >= properties.getRequestsPerMinute()) {
                throw Errors.of(PlatformErrorCode.TOO_MANY_REQUESTS);
            }
            deque.addLast(now);
        }
        return true;
    }

    private String resolveClientKey(HttpServletRequest request) {
        if (UserContext.get() != null && UserContext.currentUserId() != null) {
            return "u:" + UserContext.currentUserId();
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return "ip:" + forwarded.split(",")[0].trim();
        }
        return "ip:" + request.getRemoteAddr();
    }
}
