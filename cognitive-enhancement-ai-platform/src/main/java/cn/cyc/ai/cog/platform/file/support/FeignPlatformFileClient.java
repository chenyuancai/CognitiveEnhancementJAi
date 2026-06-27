package cn.cyc.ai.cog.platform.file.support;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.base.api.file.BaseFileFeignClient;
import cn.cyc.ai.cog.base.api.file.FileEnsureRequest;
import cn.cyc.ai.cog.base.api.file.FileInfoDTO;
import cn.cyc.ai.cog.base.api.file.FileUploadBytesRequest;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.file.spi.PlatformFileClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * 通过 Feign 调用 base-server 文件服务。
 */
@Component
@ConditionalOnBean(BaseFileFeignClient.class)
public class FeignPlatformFileClient implements PlatformFileClient {

    private final BaseFileFeignClient baseFileFeignClient;

    public FeignPlatformFileClient(BaseFileFeignClient baseFileFeignClient) {
        this.baseFileFeignClient = baseFileFeignClient;
    }

    @Override
    public FileInfoDTO uploadMultipart(Long tenantId, String bizCode, MultipartFile file) {
        return unwrap(baseFileFeignClient.upload(file, tenantId, bizCode));
    }

    @Override
    public Long uploadText(Long tenantId, String bizCode, String fileName, String text, String contentType) {
        FileUploadBytesRequest request = new FileUploadBytesRequest();
        request.setTenantId(tenantId);
        request.setBizCode(bizCode);
        request.setFileName(fileName);
        request.setContentType(contentType);
        request.setBase64Content(Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)));
        return unwrap(baseFileFeignClient.uploadBytes(request)).getId();
    }

    @Override
    public String readText(Long fileId) {
        byte[] bytes = baseFileFeignClient.downloadBytes(fileId).getBody();
        if (bytes == null || bytes.length == 0) {
            throw Errors.of(PlatformErrorCode.NOT_FOUND, "文件内容为空: " + fileId);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public FileInfoDTO getById(Long fileId) {
        return unwrap(baseFileFeignClient.getById(fileId));
    }

    @Override
    public void ensure(List<Long> fileIds) {
        FileEnsureRequest request = new FileEnsureRequest();
        request.setIds(fileIds);
        unwrapVoid(baseFileFeignClient.ensure(request));
    }

    private <T> T unwrap(ApiResponse<T> response) {
        if (response == null || !response.success()) {
            String message = response == null ? "base 文件服务无响应" : response.message();
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, message);
        }
        return response.data();
    }

    private void unwrapVoid(ApiResponse<Void> response) {
        if (response == null || !response.success()) {
            String message = response == null ? "base 文件服务无响应" : response.message();
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, message);
        }
    }
}
