package cn.cyc.ai.cog.runtime.file.spi;

import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;

import java.util.Optional;

/**
 * 文件上传记录仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface FileUploadRecordRepository {

    /**
     * 保存文件上传记录。
     *
     * @param record 文件上传记录
     */
    void save(FileUploadRecord record);

    /**
     * 按文件 ID 查询当前租户上传记录。
     *
     * @param fileId 文件 ID
     * @return 上传记录
     */
    Optional<FileUploadRecord> findByFileId(String fileId);
}
