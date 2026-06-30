package cn.cyc.ai.cog.runtime.file.spi;

import cn.cyc.ai.cog.runtime.file.domain.FileParseTask;

import java.util.Optional;

/**
 * 文件解析任务仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface FileParseTaskRepository {

    /**
     * 保存文件解析任务。
     *
     * @param task 文件解析任务
     */
    void save(FileParseTask task);

    /**
     * 查询当前租户指定文件最近一次成功的解析任务。
     *
     * @param fileId 文件 ID
     * @return 解析任务
     */
    Optional<FileParseTask> findLatestSucceededByFileId(String fileId);
}
