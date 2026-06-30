package cn.cyc.ai.cog.infra.web;

import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.core.trace.TraceIdGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 为进入 Web 请求建立最小 Trace 上下文。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class TraceContextFilter extends OncePerRequestFilter {

    /**
     * Trace 过滤器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(TraceContextFilter.class);

    /**
     * 透传请求链路标识的请求头。
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * TraceId 生成器。
     */
    private final TraceIdGenerator traceIdGenerator;

    /**
     * 构造 Trace 过滤器。
     *
     * @param traceIdGenerator TraceId 生成器
     */
    public TraceContextFilter(TraceIdGenerator traceIdGenerator) {
        this.traceIdGenerator = traceIdGenerator;
    }

    /**
     * 为每个请求建立并清理 Trace 上下文。
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (!StringUtils.hasText(traceId)) {
            traceId = traceIdGenerator.generate();
            log.debug("请求未携带 traceId，已生成新 traceId, method={}, uri={}, traceId={}",
                    request.getMethod(), request.getRequestURI(), traceId);
        } else {
            log.debug("请求携带 traceId，直接复用, method={}, uri={}, traceId={}",
                    request.getMethod(), request.getRequestURI(), traceId);
        }

        TraceContext.setTraceId(traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TraceContext.clear();
        }
    }
}
