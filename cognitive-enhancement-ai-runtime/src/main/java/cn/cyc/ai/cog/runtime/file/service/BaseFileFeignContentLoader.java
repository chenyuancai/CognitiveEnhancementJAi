package cn.cyc.ai.cog.runtime.file.service;

import cn.cyc.ai.cog.base.api.file.BaseFileFeignClient;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;
import cn.cyc.ai.cog.runtime.file.spi.FileContentLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * 从基础文件中心读取正文。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Order(10)
@Component
@ConditionalOnBean(BaseFileFeignClient.class)
public class BaseFileFeignContentLoader implements FileContentLoader {

    /** BASE文件PREFIX。 */
    private static final String BASE_FILE_PREFIX = "base:";

    /** base文件Feign客户端。 */
    private final BaseFileFeignClient baseFileFeignClient;

    /**
     * 创建Base文件Feign内容加载器。
     *
     * @param baseFileFeignClient base文件Feign客户端
     */
    public BaseFileFeignContentLoader(BaseFileFeignClient baseFileFeignClient) {
        this.baseFileFeignClient = baseFileFeignClient;
    }

    /**
     * 执行supports。
     *
     * @param upload upload
     * @return 执行结果
     */
    @Override
    public boolean supports(FileUploadRecord upload) {
        return StringUtils.hasText(upload.storagePath()) && upload.storagePath().startsWith(BASE_FILE_PREFIX);
    }

    /**
     * 执行readText。
     *
     * @param upload upload
     * @return 执行结果
     */
    @Override
    public String readText(FileUploadRecord upload) {
        Long fileId = parseBaseFileId(upload.storagePath());
        ResponseEntity<byte[]> response = baseFileFeignClient.downloadBytes(fileId);
        byte[] body = response.getBody();
        if (body == null) {
            throw new BusinessException("NOT_FOUND", "基础文件内容为空: " + fileId);
        }
        return new String(body, StandardCharsets.UTF_8);
    }

    /**
     * 执行parseBase文件ID。
     *
     * @param storagePath storage路径
     * @return 执行结果
     */
    private Long parseBaseFileId(String storagePath) {
        String rawId = storagePath.substring(BASE_FILE_PREFIX.length()).trim();
        if (!StringUtils.hasText(rawId)) {
            throw new BusinessException("BAD_REQUEST", "基础文件 ID 为空: " + storagePath);
        }
        try {
            return Long.valueOf(rawId);
        } catch (NumberFormatException ex) {
            throw new BusinessException("BAD_REQUEST", "基础文件 ID 格式错误: " + storagePath, ex);
        }
    }
}
