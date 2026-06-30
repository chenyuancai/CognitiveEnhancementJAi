package cn.cyc.ai.cog.runtime.trace.spi;

import cn.cyc.ai.cog.runtime.trace.domain.TraceSpan;

import java.util.List;

/**
 * TraceSpan 仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface TraceSpanRepository {

    /**
     * 保存 Span。
     *
     * @param span Span 记录
     */
    void save(TraceSpan span);

    /**
     * 查询当前租户全部 Span（按记录时间倒序）。
     *
     * @return Span 列表
     */
    List<TraceSpan> listAll();

    /**
     * 按 traceId 查询 Span 列表。
     *
     * @param traceId 链路 ID
     * @return Span 列表
     */
    List<TraceSpan> findByTraceId(String traceId);
}
