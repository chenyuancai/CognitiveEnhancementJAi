package cn.cyc.ai.cog.runtime.file.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 文件上传记录。
 *
 * @param tenantCode   租户编码
 * @param fileId       文件 ID
 * @param fileName     文件名
 * @param contentType  内容类型
 * @param sizeBytes    文件大小（字节）
 * @param storagePath  存储路径
 * @param checksum     校验和
 * @param status       上传状态
 * @param recordedAt   记录时间
 * @author cyc
 */
public record FileUploadRecord(
        String tenantCode,
        String fileId,
        String fileName,
        String contentType,
        long sizeBytes,
        String storagePath,
        String checksum,
        FileUploadStatus status,
        Instant recordedAt
) {

    public FileUploadRecord {
        tenantCode = TenantContext.normalize(tenantCode);
    }
}
