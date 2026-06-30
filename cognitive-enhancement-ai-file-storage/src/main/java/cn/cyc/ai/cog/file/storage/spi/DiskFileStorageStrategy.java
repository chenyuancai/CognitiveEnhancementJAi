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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class DiskFileStorageStrategy implements FileStorageStrategy {

    /** MONTHDIR。 */
    private static final DateTimeFormatter MONTH_DIR = DateTimeFormatter.ofPattern("yyyyMM");

    /** properties。 */
    private final FileStorageProperties properties;

    /**
     * 创建DiskFileStorageStrategy。
     *
     * @param properties properties
     */
    public DiskFileStorageStrategy(FileStorageProperties properties) {
        this.properties = properties;
    }

    /**
     * 执行store。
     * @return 执行结果
     */
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

    /**
     * 删除Item。
     *
     * @param storagePath storage路径
     */
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

    /**
     * 执行readBytes。
     *
     * @param storagePath storage路径
     * @return 执行结果
     */
    @Override
    public byte[] readBytes(String storagePath) {
        Path path = resolveAbsolutePath(storagePath);
        try {
            return Files.readAllBytes(path);
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "读取文件失败");
        }
    }

    /**
     * 执行openAsResource。
     *
     * @param storagePath storage路径
     * @return 执行结果
     */
    @Override
    public Resource openAsResource(String storagePath) {
        return new FileSystemResource(resolveAbsolutePath(storagePath));
    }

    /**
     * 执行resolveAbsolute路径。
     *
     * @param storagePath storage路径
     * @return 执行结果
     */
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

    /**
     * 执行resolveRoot。
     * @return 执行结果
     */
    private Path resolveRoot() {
        return Path.of(properties.resolveDiskRootPath()).toAbsolutePath().normalize();
    }

    /**
     * 执行sanitize文件名称。
     *
     * @param originalName original名称
     * @return 执行结果
     */
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
