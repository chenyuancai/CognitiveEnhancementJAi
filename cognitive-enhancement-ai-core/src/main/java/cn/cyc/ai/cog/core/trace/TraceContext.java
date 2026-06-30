package cn.cyc.ai.cog.core.trace;

/**
 * 基于线程上下文的最小 Trace 容器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class TraceContext {

    /** 链路IDHOLDER。 */
    private static final ThreadLocal<String> TRACE_ID_HOLDER = new ThreadLocal<>();

    /**
     * 创建TraceContext。
     */
    private TraceContext() {
    }

    /**
     * 设置链路ID。
     *
     * @param traceId 链路 Trace ID
     */
    public static void setTraceId(String traceId) {
        TRACE_ID_HOLDER.set(traceId);
    }

    /**
     * 获取链路ID。
     * @return 链路ID
     */
    public static String getTraceId() {
        return TRACE_ID_HOLDER.get();
    }

    /**
     * 获取Or创建链路ID。
     *
     * @param traceIdGenerator 链路IDGenerator
     * @return Or创建链路ID
     */
    public static String getOrCreateTraceId(TraceIdGenerator traceIdGenerator) {
        String current = getTraceId();
        if (current != null && !current.isBlank()) {
            return current;
        }
        String generated = traceIdGenerator.generate();
        setTraceId(generated);
        return generated;
    }

    /**
     * 执行clear。
     */
    public static void clear() {
        TRACE_ID_HOLDER.remove();
    }
}
