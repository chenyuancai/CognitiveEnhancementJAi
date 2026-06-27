package cn.cyc.ai.cog.runtime.file.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;
import cn.cyc.ai.cog.runtime.file.spi.FileContentParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

/**
 * 默认文件正文解析器。
 */
@Component
public class DefaultFileContentParser implements FileContentParser {

    private static final Set<String> TEXT_EXTENSIONS = Set.of(
            ".txt", ".md", ".markdown", ".json", ".csv", ".tsv", ".xml", ".html", ".htm", ".log"
    );

    @Override
    public String parseText(FileUploadRecord upload) {
        if (!StringUtils.hasText(upload.storagePath())) {
            throw new BusinessException("BAD_REQUEST", "文件存储路径为空，无法解析: " + upload.fileId());
        }
        if (!isSupportedTextFile(upload)) {
            throw new BusinessException("BAD_REQUEST", "暂不支持解析该文件类型: " + upload.contentType());
        }
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

    private boolean isSupportedTextFile(FileUploadRecord upload) {
        String contentType = upload.contentType();
        if (StringUtils.hasText(contentType)) {
            String normalized = contentType.toLowerCase(Locale.ROOT);
            if (normalized.startsWith("text/")
                    || normalized.contains("json")
                    || normalized.contains("xml")
                    || normalized.contains("csv")) {
                return true;
            }
        }
        String fileName = upload.fileName();
        if (!StringUtils.hasText(fileName)) {
            return false;
        }
        String normalizedName = fileName.toLowerCase(Locale.ROOT);
        return TEXT_EXTENSIONS.stream().anyMatch(normalizedName::endsWith);
    }
}
