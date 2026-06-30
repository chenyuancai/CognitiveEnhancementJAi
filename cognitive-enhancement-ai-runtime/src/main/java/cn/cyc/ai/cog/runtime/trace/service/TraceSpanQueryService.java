package cn.cyc.ai.cog.runtime.trace.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpan;
import cn.cyc.ai.cog.runtime.trace.spi.TraceSpanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TraceSpan 查询服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class TraceSpanQueryService {

    /** 链路Span仓储。 */
    private final TraceSpanRepository traceSpanRepository;

    /**
     * 创建链路Span查询服务。
     *
     * @param traceSpanRepository 链路Span仓储
     */
    public TraceSpanQueryService(TraceSpanRepository traceSpanRepository) {
        this.traceSpanRepository = traceSpanRepository;
    }

    /**
     * 按 traceId 查询 Span 列表。
     *
     * @param traceId 链路 ID
     * @return Span 列表
     */
    public List<TraceSpan> listByTraceId(String traceId) {
        if (traceId == null || traceId.isBlank()) {
            throw new BusinessException("INVALID_ARGUMENT", "traceId 不能为空");
        }
        List<TraceSpan> spans = traceSpanRepository.findByTraceId(traceId);
        if (spans.isEmpty()) {
            throw new BusinessException("NOT_FOUND", "未找到 Trace Span: " + traceId);
        }
        return spans;
    }
}
