package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.runtime.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.spi.UsageRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基于内存的用量记录仓储。
 *
 * @author cyc
 */
@Component
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
        return new ArrayList<>(records);
    }
}
