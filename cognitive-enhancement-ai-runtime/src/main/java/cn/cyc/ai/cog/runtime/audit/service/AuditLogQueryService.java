package cn.cyc.ai.cog.runtime.audit.service;

import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;
import cn.cyc.ai.cog.runtime.audit.spi.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 审计日志查询服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AuditLogQueryService {

    /** auditLog仓储。 */
    private final AuditLogRepository auditLogRepository;

    /**
     * 创建AuditLog查询服务。
     *
     * @param auditLogRepository auditLog仓储
     */
    public AuditLogQueryService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * 分页查询审计日志。
     */
    public RuntimeListResult<AuditLogRecord> listAuditLogs(String traceId,
                                                           String eventType,
                                                           String action,
                                                           String resourceType,
                                                           String resourceCode,
                                                           Boolean success,
                                                           Instant startTime,
                                                           Instant endTime,
                                                           int page,
                                                           int size,
                                                           String sort) {
        List<AuditLogRecord> filtered = auditLogRepository.listAll().stream()
                .filter(record -> matches(record.traceId(), traceId))
                .filter(record -> matches(record.eventType(), eventType))
                .filter(record -> matches(record.action(), action))
                .filter(record -> matches(record.resourceType(), resourceType))
                .filter(record -> matches(record.resourceCode(), resourceCode))
                .filter(record -> success == null || record.success() == success)
                .filter(record -> matchesTimeRange(record.recordedAt(), startTime, endTime))
                .sorted(resolveComparator(sort))
                .toList();

        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int fromIndex = Math.min((safePage - 1) * safeSize, filtered.size());
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<AuditLogRecord> items = filtered.subList(fromIndex, toIndex);
        return new RuntimeListResult<>(filtered.size(), items);
    }

    /**
     * 执行resolveComparator。
     *
     * @param sort sort
     * @return 执行结果
     */
    private Comparator<AuditLogRecord> resolveComparator(String sort) {
        if (sort != null && sort.startsWith("recordedAt,asc")) {
            return Comparator.comparing(AuditLogRecord::recordedAt);
        }
        return Comparator.comparing(AuditLogRecord::recordedAt).reversed();
    }

    /**
     * 执行matches。
     *
     * @param actual actual
     * @param expected expected
     * @return 执行结果
     */
    private boolean matches(String actual, String expected) {
        if (expected == null || expected.isBlank()) {
            return true;
        }
        return Objects.equals(actual, expected);
    }

    /**
     * 执行matches时间Range。
     *
     * @param recordedAt recordedAt
     * @param startTime start时间
     * @param endTime end时间
     * @return 执行结果
     */
    private boolean matchesTimeRange(Instant recordedAt, Instant startTime, Instant endTime) {
        if (recordedAt == null) {
            return false;
        }
        if (startTime != null && recordedAt.isBefore(startTime)) {
            return false;
        }
        if (endTime != null && recordedAt.isAfter(endTime)) {
            return false;
        }
        return true;
    }
}
