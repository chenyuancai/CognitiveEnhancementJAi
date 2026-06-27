package cn.cyc.ai.cog.runtime.audit.repository;

import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;
import cn.cyc.ai.cog.runtime.audit.spi.AuditLogRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基于内存的审计日志仓储。
 *
 * @author cyc
 */
@Component
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryAuditLogRepository implements AuditLogRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(InMemoryAuditLogRepository.class);

    /**
     * 内存记录容器。
     */
    private final CopyOnWriteArrayList<AuditLogRecord> records = new CopyOnWriteArrayList<>();

    /**
     * 保存审计日志。
     *
     * @param record 审计日志
     */
    @Override
    public void save(AuditLogRecord record) {
        log.debug("保存审计日志, eventType={}, action={}, resourceCode={}",
                record.eventType(), record.action(), record.resourceCode());
        records.add(0, record);
    }

    /**
     * 查询当前租户全部审计日志。
     *
     * @return 审计日志列表
     */
    @Override
    public List<AuditLogRecord> listAll() {
        return records.stream()
                .filter(record -> TenantContext.currentTenantCode().equals(record.tenantCode()))
                .toList();
    }
}
