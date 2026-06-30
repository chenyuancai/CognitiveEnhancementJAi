package cn.cyc.ai.cog.runtime.trace.span;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpan;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanStatus;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import cn.cyc.ai.cog.runtime.trace.otel.TraceSpanExportListener;
import cn.cyc.ai.cog.runtime.trace.spi.TraceSpanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TraceSpan 记录器，基于 ThreadLocal 维护父子关系。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class TraceSpanRecorder {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(TraceSpanRecorder.class);

    /** SPANSTACK。 */
    private static final ThreadLocal<Deque<String>> SPAN_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    /** 链路Span仓储。 */
    private final TraceSpanRepository traceSpanRepository;
    /** exportListeners。 */
    private final List<TraceSpanExportListener> exportListeners;

    /**
     * 创建TraceSpanRecorder。
     */
    public TraceSpanRecorder(TraceSpanRepository traceSpanRepository,
                             List<TraceSpanExportListener> exportListeners) {
        this.traceSpanRepository = traceSpanRepository;
        this.exportListeners = exportListeners == null ? List.of() : List.copyOf(exportListeners);
    }

    /**
     * 打开一个 Span 作用域。
     *
     * @param traceId    链路 ID
     * @param spanType   步骤类型
     * @param spanName   步骤名称
     * @param attributes 扩展属性
     * @return Span 作用域
     */
    public SpanScope open(String traceId, TraceSpanType spanType, String spanName, Map<String, Object> attributes) {
        String parentSpanId = SPAN_STACK.get().peekLast();
        String spanId = TraceSpanSupport.newSpanId();
        SPAN_STACK.get().addLast(spanId);
        return new SpanScope(traceId, spanId, parentSpanId, spanType, spanName, System.nanoTime(), copyAttributes(attributes));
    }

    /**
     * 成功结束 Span。
     *
     * @param scope      作用域
     * @param attributes 附加属性
     */
    public void succeed(SpanScope scope, Map<String, Object> attributes) {
        complete(scope, TraceSpanStatus.SUCCESS, null, attributes);
    }

    /**
     * 失败结束 Span。
     *
     * @param scope      作用域
     * @param error      异常
     * @param attributes 附加属性
     */
    public void fail(SpanScope scope, Throwable error, Map<String, Object> attributes) {
        complete(scope, TraceSpanStatus.FAILED, error, attributes);
    }

    /**
     * 清理当前线程 Span 栈。
     */
    public void clear() {
        SPAN_STACK.remove();
    }

    /**
     * 执行complete。
     */
    private void complete(SpanScope scope,
                          TraceSpanStatus status,
                          Throwable error,
                          Map<String, Object> extraAttributes) {
        if (scope == null) {
            return;
        }
        long latencyMs = Math.max(0L, (System.nanoTime() - scope.startNanos()) / 1_000_000L);
        Map<String, Object> merged = new LinkedHashMap<>(scope.attributes());
        if (extraAttributes != null) {
            extraAttributes.forEach((key, value) -> {
                if (key != null && value != null) {
                    merged.put(key, value);
                }
            });
        }
        TraceSpan span = new TraceSpan(
                TenantContext.currentTenantCode(),
                scope.traceId(),
                scope.spanId(),
                scope.parentSpanId(),
                scope.spanType(),
                scope.spanName(),
                status,
                latencyMs,
                Map.copyOf(merged),
                TraceSpanSupport.truncateStackTrace(error),
                Instant.now()
        );
        traceSpanRepository.save(span);
        exportSpan(span);
        Deque<String> stack = SPAN_STACK.get();
        if (!stack.isEmpty() && scope.spanId().equals(stack.peekLast())) {
            stack.removeLast();
        }
    }

    private static Map<String, Object> copyAttributes(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> copied = new LinkedHashMap<>();
        attributes.forEach((key, value) -> {
            if (key != null && value != null) {
                copied.put(key, value);
            }
        });
        return Map.copyOf(copied);
    }

    /**
     * 执行exportSpan。
     *
     * @param span span
     */
    private void exportSpan(TraceSpan span) {
        if (exportListeners.isEmpty()) {
            return;
        }
        for (TraceSpanExportListener listener : exportListeners) {
            try {
                listener.onSpanExported(span);
            } catch (RuntimeException ex) {
                log.warn("TraceSpan 导出监听器执行失败, traceId={}, spanId={}, listener={}, reason={}",
                        span.traceId(), span.spanId(), listener.getClass().getSimpleName(), ex.getMessage());
            }
        }
    }

    /**
     * Span 作用域句柄。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record SpanScope(
            String traceId,
            String spanId,
            String parentSpanId,
            TraceSpanType spanType,
            String spanName,
            long startNanos,
            Map<String, Object> attributes
    ) {
    }
}
