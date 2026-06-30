package cn.cyc.ai.cog.file.storage.spi;

import cn.cyc.ai.cog.file.storage.domain.StoredFileObject;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * 文件二进制对象存储策略（磁盘 / MinIO / S3 等可替换实现）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface FileStorageStrategy {

    /**
     * 将输入流写入存储并返回相对路径等信息。
     */
    StoredFileObject store(InputStream inputStream, long sizeBytes, String originalName, String contentType,
                           Long tenantId, String bizCode);

    /**
     * 删除存储对象（storagePath 为相对根目录路径）。
     */
    void delete(String storagePath);

    /**
     * 读取对象字节。
     */
    byte[] readBytes(String storagePath);

    /**
     * 以 Spring Resource 形式打开对象（下载/预览）。
     */
    Resource openAsResource(String storagePath);
}
