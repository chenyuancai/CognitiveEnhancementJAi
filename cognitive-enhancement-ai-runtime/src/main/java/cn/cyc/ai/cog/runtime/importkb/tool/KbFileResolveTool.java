package cn.cyc.ai.cog.runtime.importkb.tool;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import cn.cyc.ai.cog.platform.file.spi.PlatformFileClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 解析导入文件引用，下载到本地临时路径。
 */
@Component
public class KbFileResolveTool {

    private final PlatformFileClient platformFileClient;

    public KbFileResolveTool(PlatformFileClient platformFileClient) {
        this.platformFileClient = platformFileClient;
    }

    public void resolve(ImportWorkflowState state) {
        Long fileId = state.getFileId();
        if (fileId == null && StringUtils.hasText(state.getFileUrl())
                && PlatformFileClient.isBaseFileUrl(state.getFileUrl())) {
            fileId = PlatformFileClient.parseBaseFileId(state.getFileUrl());
            state.setFileId(fileId);
        }
        if (fileId == null) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "fileId 或 base:// 文件引用不能为空");
        }
        var info = platformFileClient.getById(fileId);
        if (!StringUtils.hasText(state.getFileName())) {
            state.setFileName(info.getOriginalName());
        }
        state.setContentType(info.getContentType());
        byte[] bytes = platformFileClient.downloadBytes(fileId);
        try {
            String suffix = guessSuffix(state.getFileName(), info.getContentType());
            Path temp = Files.createTempFile("kb-import-", suffix);
            Files.write(temp, bytes);
            state.setLocalFilePath(temp);
        } catch (IOException ex) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "写入临时文件失败");
        }
        platformFileClient.ensure(java.util.List.of(fileId));
    }

    private String guessSuffix(String fileName, String contentType) {
        if (StringUtils.hasText(fileName) && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.'));
        }
        if ("application/pdf".equalsIgnoreCase(contentType)) {
            return ".pdf";
        }
        if ("text/markdown".equalsIgnoreCase(contentType)) {
            return ".md";
        }
        return ".txt";
    }
}
