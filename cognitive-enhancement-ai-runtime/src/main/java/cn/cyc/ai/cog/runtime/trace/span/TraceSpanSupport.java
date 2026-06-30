package cn.cyc.ai.cog.runtime.trace.span;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

/**
 * TraceSpan 工具方法。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class TraceSpanSupport {

    /** MAXSTACKLENGTH。 */
    private static final int MAX_STACK_LENGTH = 4096;

    /**
     * 创建链路Span支持工具。
     */
    private TraceSpanSupport() {
    }

    /**
     * 生成 Span ID。
     *
     * @return Span ID
     */
    public static String newSpanId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 截断异常栈用于持久化。
     *
     * @param throwable 异常
     * @return 栈摘要
     */
    public static String truncateStackTrace(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        String stack = writer.toString();
        if (stack.length() <= MAX_STACK_LENGTH) {
            return stack;
        }
        return stack.substring(0, MAX_STACK_LENGTH);
    }
}
