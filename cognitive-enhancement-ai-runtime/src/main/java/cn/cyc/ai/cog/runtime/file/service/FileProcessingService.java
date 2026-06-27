package cn.cyc.ai.cog.runtime.file.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.file.domain.FileParseTask;
import cn.cyc.ai.cog.runtime.file.domain.FileParseTaskStatus;
import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;
import cn.cyc.ai.cog.runtime.file.domain.FileUploadStatus;
import cn.cyc.ai.cog.runtime.file.spi.FileParseTaskRepository;
import cn.cyc.ai.cog.runtime.file.spi.FileUploadRecordRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * 文件处理服务。
 *
 * @author cyc
 */
@Service
public class FileProcessingService {

    /**
     * 文件上传记录仓储。
     */
    private final FileUploadRecordRepository fileUploadRecordRepository;

    /**
     * 文件解析任务仓储。
     */
    private final FileParseTaskRepository fileParseTaskRepository;

    /**
     * 构造文件处理服务。
     *
     * @param fileUploadRecordRepository 文件上传记录仓储
     * @param fileParseTaskRepository    文件解析任务仓储
     */
    public FileProcessingService(FileUploadRecordRepository fileUploadRecordRepository,
                                 FileParseTaskRepository fileParseTaskRepository) {
        this.fileUploadRecordRepository = fileUploadRecordRepository;
        this.fileParseTaskRepository = fileParseTaskRepository;
    }

    /**
     * 注册文件上传元数据。
     *
     * @param fileName    文件名
     * @param contentType 内容类型
     * @param sizeBytes   文件大小（字节）
     * @param storagePath 存储路径
     * @param checksum    校验和
     * @return 上传记录
     */
    public FileUploadRecord registerUpload(String fileName,
                                           String contentType,
                                           long sizeBytes,
                                           String storagePath,
                                           String checksum) {
        FileUploadRecord record = new FileUploadRecord(
                TenantContext.currentTenantCode(),
                UUID.randomUUID().toString(),
                fileName,
                contentType,
                sizeBytes,
                storagePath,
                checksum,
                FileUploadStatus.UPLOADED,
                Instant.now()
        );
        fileUploadRecordRepository.save(record);
        return record;
    }

    /**
     * 查询文件上传记录。
     *
     * @param fileId 文件 ID
     * @return 上传记录
     */
    public FileUploadRecord getUpload(String fileId) {
        return fileUploadRecordRepository.findByFileId(fileId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到文件上传记录: " + fileId));
    }

    /**
     * 启动文件解析。
     *
     * @param fileId 文件 ID
     * @return 解析任务
     */
    public FileParseTask startParse(String fileId) {
        FileUploadRecord upload = getUpload(fileId);
        if (upload.status() == FileUploadStatus.PARSED) {
            var cachedTask = fileParseTaskRepository.findLatestSucceededByFileId(fileId);
            if (cachedTask.isPresent()) {
                return cachedTask.get();
            }
        }

        Instant now = Instant.now();
        String parseResult = "{\"textPreview\":\"mock parsed: " + escapeJson(upload.fileName()) + "\",\"cached\":false}";
        FileParseTask task = new FileParseTask(
                upload.tenantCode(),
                UUID.randomUUID().toString(),
                fileId,
                FileParseTaskStatus.SUCCEEDED,
                parseResult,
                null,
                now,
                now,
                now
        );
        fileParseTaskRepository.save(task);

        FileUploadRecord parsedUpload = new FileUploadRecord(
                upload.tenantCode(),
                upload.fileId(),
                upload.fileName(),
                upload.contentType(),
                upload.sizeBytes(),
                upload.storagePath(),
                upload.checksum(),
                FileUploadStatus.PARSED,
                upload.recordedAt()
        );
        fileUploadRecordRepository.save(parsedUpload);
        return task;
    }

    /**
     * 查询最新解析结果。
     *
     * @param fileId 文件 ID
     * @return 最新成功的解析任务
     */
    public FileParseTask getLatestParseResult(String fileId) {
        getUpload(fileId);
        return fileParseTaskRepository.findLatestSucceededByFileId(fileId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到文件解析结果: " + fileId));
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
