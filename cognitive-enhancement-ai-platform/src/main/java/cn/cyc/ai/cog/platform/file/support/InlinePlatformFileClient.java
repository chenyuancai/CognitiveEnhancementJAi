package cn.cyc.ai.cog.platform.file.support;

import cn.cyc.ai.cog.base.api.file.FileInfoDTO;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.file.spi.PlatformFileClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存文件客户端：集成测试或未启用 Feign 时使用。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@ConditionalOnMissingBean(FeignPlatformFileClient.class)
public class InlinePlatformFileClient implements PlatformFileClient {

    /** IDSeq。 */
    private final AtomicLong idSeq = new AtomicLong(1);
    private final Map<Long, Stored> store = new ConcurrentHashMap<>();

    /**
     * 执行uploadText。
     *
     * @param tenantId 租户 ID
     * @param bizCode biz编码
     * @param fileName 文件名称
     * @param text text
     * @param contentType 内容类型
     * @return 执行结果
     */
    @Override
    public Long uploadText(Long tenantId, String bizCode, String fileName, String text, String contentType) {
        long id = idSeq.getAndIncrement();
        store.put(id, new Stored(tenantId, bizCode, fileName, text, contentType));
        return id;
    }

    /**
     * 执行uploadMultipart。
     *
     * @param tenantId 租户 ID
     * @param bizCode biz编码
     * @param file 文件
     * @return 执行结果
     */
    @Override
    public FileInfoDTO uploadMultipart(Long tenantId, String bizCode, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "上传文件不能为空");
        }
        try {
            byte[] bytes = file.getBytes();
            long id = idSeq.getAndIncrement();
            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            String text = new String(bytes, StandardCharsets.UTF_8);
            store.put(id, new Stored(tenantId, bizCode, fileName, text, contentType, bytes));
            return getById(id);
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "读取上传流失败");
        }
    }

    /**
     * 执行readText。
     *
     * @param fileId 文件ID
     * @return 执行结果
     */
    @Override
    public String readText(Long fileId) {
        Stored stored = store.get(fileId);
        if (stored == null) {
            throw Errors.of(PlatformErrorCode.NOT_FOUND, "内存文件不存在: " + fileId);
        }
        return stored.text();
    }

    /**
     * 获取人ID。
     *
     * @param fileId 文件ID
     * @return 人ID
     */
    @Override
    public FileInfoDTO getById(Long fileId) {
        Stored stored = store.get(fileId);
        if (stored == null) {
            throw Errors.of(PlatformErrorCode.NOT_FOUND, "内存文件不存在: " + fileId);
        }
        FileInfoDTO dto = new FileInfoDTO();
        dto.setId(fileId);
        dto.setTenantId(stored.tenantId());
        dto.setBizCode(stored.bizCode());
        dto.setOriginalName(stored.fileName());
        dto.setContentType(stored.contentType());
        dto.setSizeBytes(stored.bytes() != null
                ? (long) stored.bytes().length
                : (long) stored.text().getBytes(StandardCharsets.UTF_8).length);
        dto.setStoragePath(PlatformFileClient.toBaseFileUrl(fileId));
        return dto;
    }

    /**
     * 执行ensure。
     *
     * @param fileIds 文件Ids
     */
    @Override
    public void ensure(List<Long> fileIds) {
        // no-op for inline
    }

    @Override
    public byte[] downloadBytes(Long fileId) {
        Stored stored = store.get(fileId);
        if (stored == null) {
            throw Errors.of(PlatformErrorCode.NOT_FOUND, "内存文件不存在: " + fileId);
        }
        return stored.bytes() != null ? stored.bytes() : stored.text().getBytes(StandardCharsets.UTF_8);
    }

    private record Stored(Long tenantId, String bizCode, String fileName, String text, String contentType, byte[] bytes) {

        Stored(Long tenantId, String bizCode, String fileName, String text, String contentType) {
            this(tenantId, bizCode, fileName, text, contentType, null);
        }
    }
}
