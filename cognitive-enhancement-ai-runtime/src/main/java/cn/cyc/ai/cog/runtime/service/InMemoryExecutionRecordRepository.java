package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.runtime.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.spi.ExecutionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基于内存的执行记录仓储。
 *
 * @author cyc
 */
@Component
public class InMemoryExecutionRecordRepository implements ExecutionRecordRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(InMemoryExecutionRecordRepository.class);

    /**
     * 内存记录容器。
     */
    private final CopyOnWriteArrayList<ExecutionRecord> records = new CopyOnWriteArrayList<>();

    /**
     * 保存执行记录。
     *
     * @param record 执行记录
     */
    @Override
    public void save(ExecutionRecord record) {
        log.debug("保存执行记录, traceId={}, capabilityCode={}", record.traceId(), record.capabilityCode());
        records.add(0, record);
    }

    /**
     * 查询全部执行记录。
     *
     * @return 执行记录列表
     */
    @Override
    public List<ExecutionRecord> listAll() {
        return new ArrayList<>(records);
    }
}
