package cn.cyc.ai.cog.runtime.trace.repository;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpan;
import cn.cyc.ai.cog.runtime.trace.spi.TraceSpanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 内存 TraceSpan 仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryTraceSpanRepository implements TraceSpanRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryTraceSpanRepository.class);

    private final CopyOnWriteArrayList<TraceSpan> records = new CopyOnWriteArrayList<>();

    @Override
    public void save(TraceSpan span) {
        records.add(span);
        log.debug("保存 TraceSpan, traceId={}, spanId={}, type={}, status={}",
                span.traceId(), span.spanId(), span.spanType(), span.status());
    }

    @Override
    public List<TraceSpan> listAll() {
        String tenantCode = TenantContext.currentTenantCode();
        return records.stream()
                .filter(item -> tenantCode.equals(item.tenantCode()))
                .sorted(Comparator.comparing(TraceSpan::recordedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<TraceSpan> findByTraceId(String traceId) {
        String tenantCode = TenantContext.currentTenantCode();
        return records.stream()
                .filter(item -> tenantCode.equals(item.tenantCode()))
                .filter(item -> traceId.equals(item.traceId()))
                .sorted(Comparator.comparing(TraceSpan::recordedAt))
                .collect(Collectors.toList());
    }
}
