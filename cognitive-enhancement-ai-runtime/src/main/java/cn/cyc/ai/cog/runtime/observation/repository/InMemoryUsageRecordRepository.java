package cn.cyc.ai.cog.runtime.observation.repository;

import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.observation.spi.UsageRecordRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基于内存的用量记录仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryUsageRecordRepository implements UsageRecordRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(InMemoryUsageRecordRepository.class);

    /**
     * 内存记录容器。
     */
    private final CopyOnWriteArrayList<UsageRecord> records = new CopyOnWriteArrayList<>();

    /**
     * 保存用量记录。
     *
     * @param record 用量记录
     */
    @Override
    public void save(UsageRecord record) {
        log.debug("保存用量记录, traceId={}, capabilityCode={}, executorType={}",
                record.traceId(), record.capabilityCode(), record.executorType());
        records.add(0, record);
    }

    /**
     * 查询全部用量记录。
     *
     * @return 用量记录列表
     */
    @Override
    public List<UsageRecord> listAll() {
        return records.stream()
                .filter(record -> TenantContext.currentTenantCode().equals(record.tenantCode()))
                .toList();
    }
}
