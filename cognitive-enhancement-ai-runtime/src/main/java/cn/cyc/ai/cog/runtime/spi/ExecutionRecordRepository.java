package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.runtime.domain.ExecutionRecord;

import java.util.List;

/**
 * 执行记录仓储。
 *
 * @author cyc
 */
public interface ExecutionRecordRepository {

    /**
     * 保存执行记录。
     *
     * @param record 执行记录
     */
    void save(ExecutionRecord record);

    /**
     * 查询全部执行记录。
     *
     * @return 执行记录列表
     */
    List<ExecutionRecord> listAll();
}
