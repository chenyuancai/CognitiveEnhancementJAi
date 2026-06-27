package cn.cyc.ai.cog.runtime.file.dto;

/**
 * 注册文件上传请求。
 *
 * @param fileName    文件名
 * @param contentType 内容类型
 * @param sizeBytes   文件大小（字节）
 * @param storagePath 存储路径
 * @param checksum    校验和
 * @author cyc
 */
public record RegisterFileUploadRequest(
        String fileName,
        String contentType,
        long sizeBytes,
        String storagePath,
        String checksum
) {
}
