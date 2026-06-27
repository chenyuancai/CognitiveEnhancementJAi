package cn.cyc.ai.cog.runtime.observation.spi;

import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;

import java.util.List;
import java.util.Optional;

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

    /**
     * 按 traceId 查询执行记录。
     *
     * @param traceId 链路标识
     * @return 执行记录
     */
    Optional<ExecutionRecord> findByTraceId(String traceId);
}
