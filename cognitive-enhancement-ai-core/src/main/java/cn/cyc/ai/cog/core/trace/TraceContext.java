package cn.cyc.ai.cog.core.trace;

/**
 * 基于线程上下文的最小 Trace 容器。
 */
public final class TraceContext {

    private static final ThreadLocal<String> TRACE_ID_HOLDER = new ThreadLocal<>();

    private TraceContext() {
    }

    public static void setTraceId(String traceId) {
        TRACE_ID_HOLDER.set(traceId);
    }

    public static String getTraceId() {
        return TRACE_ID_HOLDER.get();
    }

    public static String getOrCreateTraceId(TraceIdGenerator traceIdGenerator) {
        String current = getTraceId();
        if (current != null && !current.isBlank()) {
            return current;
        }
        String generated = traceIdGenerator.generate();
        setTraceId(generated);
        return generated;
    }

    public static void clear() {
        TRACE_ID_HOLDER.remove();
    }
}
