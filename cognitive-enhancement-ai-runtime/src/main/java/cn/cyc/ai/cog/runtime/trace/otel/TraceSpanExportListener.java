package cn.cyc.ai.cog.runtime.trace.otel;

import cn.cyc.ai.cog.runtime.trace.domain.TraceSpan;

/**
 * TraceSpan 导出监听器，用于对接 OpenTelemetry 等外部观测系统。
 *
 * @author cyc
 */
public interface TraceSpanExportListener {

    /**
     * Span 持久化完成后回调。
     *
     * @param span 已保存的 Span
     */
    void onSpanExported(TraceSpan span);
}
