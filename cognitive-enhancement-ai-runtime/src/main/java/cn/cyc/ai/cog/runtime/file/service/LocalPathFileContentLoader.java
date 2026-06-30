package cn.cyc.ai.cog.runtime.file.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;
import cn.cyc.ai.cog.runtime.file.spi.FileContentLoader;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 兼容旧版本地路径读取方式。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Order(100)
@Component
public class LocalPathFileContentLoader implements FileContentLoader {

    /**
     * 执行supports。
     *
     * @param upload upload
     * @return 执行结果
     */
    @Override
    public boolean supports(FileUploadRecord upload) {
        return StringUtils.hasText(upload.storagePath()) && !upload.storagePath().startsWith("base:");
    }

    /**
     * 执行readText。
     *
     * @param upload upload
     * @return 执行结果
     */
    @Override
    public String readText(FileUploadRecord upload) {
        Path path = Path.of(upload.storagePath());
        if (!Files.isRegularFile(path)) {
            throw new BusinessException("NOT_FOUND", "文件不存在或不可读取: " + upload.storagePath());
        }
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new BusinessException("CONFLICT", "读取文件失败: " + upload.fileName(), ex);
        }
    }
}
