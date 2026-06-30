package cn.cyc.ai.cog.file.storage.domain;

/**
 * 对象存储文件信息。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record OssObject(
        String bucketName,
        String objectName,
        String originalName,
        String contentType,
        long sizeBytes,
        String md5,
        String link
) {
}
