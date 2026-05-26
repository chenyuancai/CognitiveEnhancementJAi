package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.runtime.domain.ModelCheckRecord;
import cn.cyc.ai.cog.runtime.spi.ModelCheckRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基于内存的模型检查记录仓储。
 *
 * @author cyc
 */
@Component
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryModelCheckRecordRepository implements ModelCheckRecordRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(InMemoryModelCheckRecordRepository.class);

    /**
     * 内存记录容器。
     */
    private final CopyOnWriteArrayList<ModelCheckRecord> records = new CopyOnWriteArrayList<>();

    /**
     * 保存模型检查记录。
     *
     * @param record 模型检查记录
     */
    @Override
    public void save(ModelCheckRecord record) {
        log.debug("保存模型检查记录, traceId={}, providerCode={}, modelCode={}, reachable={}",
                record.traceId(), record.providerCode(), record.modelCode(), record.reachable());
        records.add(0, record);
    }

    /**
     * 查询全部模型检查记录。
     *
     * @return 模型检查记录列表
     */
    @Override
    public List<ModelCheckRecord> listAll() {
        return new ArrayList<>(records);
    }
}
