package cn.cyc.ai.cog.file.storage.spi;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.file.storage.config.FileStorageProperties;
import cn.cyc.ai.cog.file.storage.domain.StoredFileObject;
import cn.cyc.ai.cog.file.storage.support.Md5Support;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地磁盘对象存储（默认策略）。
 */
public class DiskFileStorageStrategy implements FileStorageStrategy {

    private static final DateTimeFormatter MONTH_DIR = DateTimeFormatter.ofPattern("yyyyMM");

    private final FileStorageProperties properties;

    public DiskFileStorageStrategy(FileStorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public StoredFileObject store(InputStream inputStream, long sizeBytes, String originalName, String contentType,
                                  Long tenantId, String bizCode) {
        long maxSize = properties.resolveMaxFileSize();
        if (sizeBytes > maxSize) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "文件大小超过限制: " + maxSize);
        }
        String safeName = sanitizeFileName(originalName);
        String storageName = UUID.randomUUID().toString().replace("-", "") + "_" + safeName;
        String relativeDir = tenantId + "/" + MONTH_DIR.format(LocalDate.now()) + "/"
                + (StringUtils.hasText(bizCode) ? bizCode : "default");
        Path targetDir = resolveRoot().resolve(relativeDir);
        Path targetFile = targetDir.resolve(storageName);
        try {
            Files.createDirectories(targetDir);
            byte[] bytes = inputStream.readAllBytes();
            if (bytes.length > maxSize) {
                throw Errors.of(PlatformErrorCode.BAD_REQUEST, "文件大小超过限制: " + maxSize);
            }
            String md5 = Md5Support.md5Hex(new ByteArrayInputStream(bytes));
            try (OutputStream out = Files.newOutputStream(targetFile)) {
                out.write(bytes);
            }
            String storagePath = relativeDir + "/" + storageName;
            return new StoredFileObject(storageName, storagePath, bytes.length, md5);
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "磁盘写入失败: " + exception.getMessage());
        }
    }

    @Override
    public void delete(String storagePath) {
        if (!StringUtils.hasText(storagePath)) {
            return;
        }
        Path path = resolveAbsolutePath(storagePath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "磁盘删除失败: " + exception.getMessage());
        }
    }

    @Override
    public byte[] readBytes(String storagePath) {
        Path path = resolveAbsolutePath(storagePath);
        try {
            return Files.readAllBytes(path);
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "读取文件失败");
        }
    }

    @Override
    public Resource openAsResource(String storagePath) {
        return new FileSystemResource(resolveAbsolutePath(storagePath));
    }

    private Path resolveAbsolutePath(String storagePath) {
        Path path = resolveRoot().resolve(storagePath).normalize();
        if (!path.startsWith(resolveRoot())) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "非法存储路径");
        }
        if (!Files.exists(path)) {
            throw Errors.of(PlatformErrorCode.NOT_FOUND, "文件不存在或已删除");
        }
        return path;
    }

    private Path resolveRoot() {
        return Path.of(properties.resolveDiskRootPath()).toAbsolutePath().normalize();
    }

    private static String sanitizeFileName(String originalName) {
        if (!StringUtils.hasText(originalName)) {
            return "file";
        }
        String name = originalName.replace("\\", "/");
        int slash = name.lastIndexOf('/');
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        name = name.replaceAll("[^a-zA-Z0-9._\\-一-龥]", "_");
        return name.isBlank() ? "file" : name;
    }
}
