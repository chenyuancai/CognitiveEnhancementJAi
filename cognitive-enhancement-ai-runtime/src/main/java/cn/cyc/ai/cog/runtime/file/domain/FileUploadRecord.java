package cn.cyc.ai.cog.runtime.file.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 文件上传记录。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
