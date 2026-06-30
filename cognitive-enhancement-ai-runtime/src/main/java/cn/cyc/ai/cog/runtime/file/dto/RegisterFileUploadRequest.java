package cn.cyc.ai.cog.runtime.file.dto;

/**
 * 注册文件上传请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record RegisterFileUploadRequest(
        String fileName,
        String contentType,
        long sizeBytes,
        String storagePath,
        String checksum
) {
}
