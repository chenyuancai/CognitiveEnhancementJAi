package cn.cyc.ai.cog.file.storage.domain;

/**
 * 对象存储落盘结果（相对 storagePath + 摘要信息）。
 */
public record StoredFileObject(
        String storageName,
        String storagePath,
        long sizeBytes,
        String md5
) {
}
