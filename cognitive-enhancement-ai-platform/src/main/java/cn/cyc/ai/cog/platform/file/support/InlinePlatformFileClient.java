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
 */
@Component
@ConditionalOnMissingBean(FeignPlatformFileClient.class)
public class InlinePlatformFileClient implements PlatformFileClient {

    private final AtomicLong idSeq = new AtomicLong(1);
    private final Map<Long, Stored> store = new ConcurrentHashMap<>();

    @Override
    public Long uploadText(Long tenantId, String bizCode, String fileName, String text, String contentType) {
        long id = idSeq.getAndIncrement();
        store.put(id, new Stored(tenantId, bizCode, fileName, text, contentType));
        return id;
    }

    @Override
    public FileInfoDTO uploadMultipart(Long tenantId, String bizCode, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "上传文件不能为空");
        }
        try {
            String text = new String(file.getBytes(), StandardCharsets.UTF_8);
            Long id = uploadText(tenantId, bizCode,
                    file.getOriginalFilename() != null ? file.getOriginalFilename() : "file",
                    text,
                    file.getContentType() != null ? file.getContentType() : "application/octet-stream");
            return getById(id);
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "读取上传流失败");
        }
    }

    @Override
    public String readText(Long fileId) {
        Stored stored = store.get(fileId);
        if (stored == null) {
            throw Errors.of(PlatformErrorCode.NOT_FOUND, "内存文件不存在: " + fileId);
        }
        return stored.text();
    }

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
        dto.setSizeBytes((long) stored.text().getBytes(StandardCharsets.UTF_8).length);
        dto.setStoragePath(PlatformFileClient.toBaseFileUrl(fileId));
        return dto;
    }

    @Override
    public void ensure(List<Long> fileIds) {
        // no-op for inline
    }

    private record Stored(Long tenantId, String bizCode, String fileName, String text, String contentType) {
    }
}
