package cn.cyc.ai.cog.platform.file.spi;

import cn.cyc.ai.cog.base.api.file.FileInfoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 平台侧文件访问 SPI：由 admin/app 进程通过 Feign 对接 base-server，测试环境可换内存实现。
 */
public interface PlatformFileClient {

    String BASE_FILE_URL_PREFIX = "base://";

    /**
     * Multipart 上传（C 端直传等场景）。
     */
    FileInfoDTO uploadMultipart(Long tenantId, String bizCode, MultipartFile file);

    /**
     * 上传文本内容为文件（如 CSV 导入）。
     */
    Long uploadText(Long tenantId, String bizCode, String fileName, String text, String contentType);

    /**
     * 按 base 文件 ID 读取 UTF-8 文本。
     */
    String readText(Long fileId);

    /**
     * 查询文件元数据。
     */
    FileInfoDTO getById(Long fileId);

    /**
     * 业务确认后调用，避免孤儿文件被清理。
     */
    void ensure(List<Long> fileIds);

    static boolean isBaseFileUrl(String fileUrl) {
        return fileUrl != null && fileUrl.startsWith(BASE_FILE_URL_PREFIX);
    }

    static Long parseBaseFileId(String fileUrl) {
        if (!isBaseFileUrl(fileUrl)) {
            throw new IllegalArgumentException("非 base 文件引用: " + fileUrl);
        }
        return Long.parseLong(fileUrl.substring(BASE_FILE_URL_PREFIX.length()));
    }

    static String toBaseFileUrl(Long fileId) {
        return BASE_FILE_URL_PREFIX + fileId;
    }
}
