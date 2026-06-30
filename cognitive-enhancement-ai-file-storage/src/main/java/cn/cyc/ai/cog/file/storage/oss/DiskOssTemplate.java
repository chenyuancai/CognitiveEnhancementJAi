package cn.cyc.ai.cog.file.storage.oss;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.file.storage.config.FileStorageProperties;
import cn.cyc.ai.cog.file.storage.domain.OssObject;
import cn.cyc.ai.cog.file.storage.support.Md5Support;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 本地磁盘对象存储模板。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class DiskOssTemplate implements OssTemplate {

    /** properties。 */
    private final FileStorageProperties properties;
    /** ossRule。 */
    private final OssRule ossRule;

    /**
     * 创建DiskOssTemplate。
     *
     * @param properties properties
     * @param ossRule ossRule
     */
    public DiskOssTemplate(FileStorageProperties properties, OssRule ossRule) {
        this.properties = properties;
        this.ossRule = ossRule;
    }

    /**
     * 执行putObject。
     * @return 执行结果
     */
    @Override
    public OssObject putObject(String bucketName,
                               String originalName,
                               InputStream inputStream,
                               long sizeBytes,
                               String contentType) {
        long maxSize = properties.resolveMaxFileSize();
        if (sizeBytes > maxSize) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "文件大小超过限制: " + maxSize);
        }
        String resolvedBucket = ossRule.bucketName(bucketName);
        String objectName = ossRule.objectName(originalName);
        Path targetFile = resolveObjectPath(resolvedBucket, objectName, false);
        try {
            Files.createDirectories(targetFile.getParent());
            byte[] bytes = inputStream.readAllBytes();
            if (bytes.length > maxSize) {
                throw Errors.of(PlatformErrorCode.BAD_REQUEST, "文件大小超过限制: " + maxSize);
            }
            String md5 = Md5Support.md5Hex(new ByteArrayInputStream(bytes));
            try (OutputStream out = Files.newOutputStream(targetFile)) {
                out.write(bytes);
            }
            return new OssObject(resolvedBucket, objectName, safeOriginalName(originalName),
                    resolveContentType(contentType, originalName), bytes.length, md5, objectLink(resolvedBucket, objectName));
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "磁盘写入失败: " + exception.getMessage());
        }
    }

    /**
     * 执行statObject。
     *
     * @param bucketName bucket名称
     * @param objectName object名称
     * @return 执行结果
     */
    @Override
    public OssObject statObject(String bucketName, String objectName) {
        String resolvedBucket = ossRule.bucketName(bucketName);
        Path path = resolveObjectPath(resolvedBucket, objectName, true);
        try {
            String md5 = Md5Support.md5Hex(new ByteArrayInputStream(Files.readAllBytes(path)));
            return new OssObject(resolvedBucket, objectName, path.getFileName().toString(),
                    resolveContentType(null, objectName), Files.size(path), md5, objectLink(resolvedBucket, objectName));
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "读取文件信息失败");
        }
    }

    /**
     * 执行readBytes。
     *
     * @param bucketName bucket名称
     * @param objectName object名称
     * @return 执行结果
     */
    @Override
    public byte[] readBytes(String bucketName, String objectName) {
        Path path = resolveObjectPath(ossRule.bucketName(bucketName), objectName, true);
        try {
            return Files.readAllBytes(path);
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "读取文件失败");
        }
    }

    /**
     * 执行openAsResource。
     *
     * @param bucketName bucket名称
     * @param objectName object名称
     * @return 执行结果
     */
    @Override
    public Resource openAsResource(String bucketName, String objectName) {
        return new FileSystemResource(resolveObjectPath(ossRule.bucketName(bucketName), objectName, true));
    }

    /**
     * 删除Object。
     *
     * @param bucketName bucket名称
     * @param objectName object名称
     */
    @Override
    public void removeObject(String bucketName, String objectName) {
        if (!StringUtils.hasText(objectName)) {
            return;
        }
        Path path = resolveObjectPath(ossRule.bucketName(bucketName), objectName, true);
        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "磁盘删除失败: " + exception.getMessage());
        }
    }

    /**
     * 执行object路径。
     *
     * @param bucketName bucket名称
     * @param objectName object名称
     * @return 执行结果
     */
    @Override
    public String objectPath(String bucketName, String objectName) {
        return ossRule.bucketName(bucketName) + "/" + objectName;
    }

    /**
     * 执行objectLink。
     *
     * @param bucketName bucket名称
     * @param objectName object名称
     * @return 执行结果
     */
    @Override
    public String objectLink(String bucketName, String objectName) {
        return objectPath(bucketName, objectName);
    }

    /**
     * 执行resolveObject路径。
     *
     * @param bucketName bucket名称
     * @param objectName object名称
     * @param requireExists requireExists
     * @return 执行结果
     */
    private Path resolveObjectPath(String bucketName, String objectName, boolean requireExists) {
        if (!StringUtils.hasText(objectName)) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "对象名称不能为空");
        }
        Path root = resolveRoot();
        Path path = root.resolve(bucketName).resolve(objectName).normalize();
        if (!path.startsWith(root)) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "非法存储路径");
        }
        if (requireExists && !Files.exists(path)) {
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
     * 执行resolve内容类型。
     *
     * @param contentType 内容类型
     * @param originalName original名称
     * @return 执行结果
     */
    private static String resolveContentType(String contentType, String originalName) {
        if (StringUtils.hasText(contentType)) {
            return contentType;
        }
        String guessed = URLConnection.guessContentTypeFromName(originalName);
        return StringUtils.hasText(guessed) ? guessed : "application/octet-stream";
    }

    /**
     * 执行safeOriginal名称。
     *
     * @param originalName original名称
     * @return 执行结果
     */
    private static String safeOriginalName(String originalName) {
        if (!StringUtils.hasText(originalName)) {
            return "file";
        }
        String name = originalName.replace("\\", "/");
        int slash = name.lastIndexOf('/');
        return slash >= 0 ? name.substring(slash + 1) : name;
    }
}
