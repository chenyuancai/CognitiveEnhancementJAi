package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.runtime.domain.ModelCheckRecord;

import java.util.List;

/**
 * 模型检查记录仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ModelCheckRecordRepository {

    /**
     * 保存模型检查记录。
     *
     * @param record 模型检查记录
     */
    void save(ModelCheckRecord record);

    /**
     * 查询全部模型检查记录。
     *
     * @return 模型检查记录列表
     */
    List<ModelCheckRecord> listAll();
}
