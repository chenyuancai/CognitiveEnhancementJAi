package cn.cyc.ai.cog.file.storage.domain;

/**
 * 对象存储落盘结果（相对 storagePath + 摘要信息）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record StoredFileObject(
        String storageName,
        String storagePath,
        long sizeBytes,
        String md5
) {
}
