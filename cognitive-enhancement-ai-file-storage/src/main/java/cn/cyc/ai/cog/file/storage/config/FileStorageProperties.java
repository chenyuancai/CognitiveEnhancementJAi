package cn.cyc.ai.cog.file.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件对象存储配置（对齐 ztx3 {@code ztx.oss.name} 模式，通过 {@code cog.file.storage-type} 切换策略）。
 */
@Data
@ConfigurationProperties(prefix = "cog.file")
public class FileStorageProperties {

    /** 存储类型：disk（默认）/ minio（预留） */
    private String storageType = "disk";

    /** 单文件最大字节数，默认 100MB */
    private long maxFileSize = 104_857_600L;

    private Disk disk = new Disk();

    @Data
    public static class Disk {

        /** 本地磁盘根目录 */
        private String rootPath = System.getProperty("user.home") + "/cog-files";

        /** 兼容旧配置 cog.file.disk.max-file-size */
        private Long maxFileSize;
    }

    public long resolveMaxFileSize() {
        if (disk != null && disk.getMaxFileSize() != null && disk.getMaxFileSize() > 0) {
            return disk.getMaxFileSize();
        }
        return maxFileSize > 0 ? maxFileSize : 104_857_600L;
    }

    public String resolveDiskRootPath() {
        return disk != null && disk.getRootPath() != null && !disk.getRootPath().isBlank()
                ? disk.getRootPath()
                : System.getProperty("user.home") + "/cog-files";
    }
}
