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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@ConditionalOnBean(BaseFileFeignClient.class)
public class FeignPlatformFileClient implements PlatformFileClient {

    /** base文件Feign客户端。 */
    private final BaseFileFeignClient baseFileFeignClient;

    /**
     * 创建Feign平台文件客户端。
     *
     * @param baseFileFeignClient base文件Feign客户端
     */
    public FeignPlatformFileClient(BaseFileFeignClient baseFileFeignClient) {
        this.baseFileFeignClient = baseFileFeignClient;
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
        return unwrap(baseFileFeignClient.upload(file, tenantId, bizCode));
    }

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
        FileUploadBytesRequest request = new FileUploadBytesRequest();
        request.setTenantId(tenantId);
        request.setBizCode(bizCode);
        request.setFileName(fileName);
        request.setContentType(contentType);
        request.setBase64Content(Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)));
        return unwrap(baseFileFeignClient.uploadBytes(request)).getId();
    }

    /**
     * 执行readText。
     *
     * @param fileId 文件ID
     * @return 执行结果
     */
    @Override
    public String readText(Long fileId) {
        byte[] bytes = downloadBytes(fileId);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] downloadBytes(Long fileId) {
        byte[] bytes = baseFileFeignClient.downloadBytes(fileId).getBody();
        if (bytes == null || bytes.length == 0) {
            throw Errors.of(PlatformErrorCode.NOT_FOUND, "文件内容为空: " + fileId);
        }
        return bytes;
    }

    /**
     * 获取人ID。
     *
     * @param fileId 文件ID
     * @return 人ID
     */
    @Override
    public FileInfoDTO getById(Long fileId) {
        return unwrap(baseFileFeignClient.getById(fileId));
    }

    /**
     * 执行ensure。
     *
     * @param fileIds 文件Ids
     */
    @Override
    public void ensure(List<Long> fileIds) {
        FileEnsureRequest request = new FileEnsureRequest();
        request.setIds(fileIds);
        unwrapVoid(baseFileFeignClient.ensure(request));
    }

    /**
     * 执行unwrap。
     *
     * @param response 响应
     * @return 执行结果
     */
    private <T> T unwrap(ApiResponse<T> response) {
        if (response == null || !response.success()) {
            String message = response == null ? "base 文件服务无响应" : response.message();
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, message);
        }
        return response.data();
    }

    /**
     * 执行unwrapVoid。
     *
     * @param response 响应
     */
    private void unwrapVoid(ApiResponse<Void> response) {
        if (response == null || !response.success()) {
            String message = response == null ? "base 文件服务无响应" : response.message();
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, message);
        }
    }
}
