package cn.cyc.ai.cog.runtime.observation.spi;

import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;

import java.util.List;

/**
 * 用量记录仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface UsageRecordRepository {

    /**
     * 保存用量记录。
     *
     * @param record 用量记录
     */
    void save(UsageRecord record);

    /**
     * 查询全部用量记录。
     *
     * @return 用量记录列表
     */
    List<UsageRecord> listAll();
}
