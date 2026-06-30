package cn.cyc.ai.cog.runtime.file.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.file.domain.FileParseTask;
import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;
import cn.cyc.ai.cog.runtime.file.dto.FileParseRequest;
import cn.cyc.ai.cog.runtime.file.dto.RegisterFileUploadRequest;
import cn.cyc.ai.cog.runtime.file.service.FileProcessingService;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime 文件处理接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Runtime - 文件", description = "文件上传元数据与解析任务")
@RestController
@RequestMapping("/api/runtime/files")
public class RuntimeFileController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(RuntimeFileController.class);

    /**
     * 文件处理服务。
     */
    private final FileProcessingService fileProcessingService;

    /**
     * 构造 Runtime 文件处理接口。
     *
     * @param fileProcessingService 文件处理服务
     */
    public RuntimeFileController(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    /**
     * 注册文件上传元数据。
     *
     * @param request 注册文件上传请求
     * @return 上传记录
     */
    @Operation(summary = "注册文件上传", description = "登记文件名/类型/大小/存储路径等上传元数据，返回上传记录。")
    @PostMapping
    public ApiResponse<FileUploadRecord> registerUpload(@RequestBody RegisterFileUploadRequest request) {
        log.info("收到文件上传注册请求, fileName={}", request.fileName());
        return RuntimeResponses.success(fileProcessingService.registerUpload(
                request.fileName(),
                request.contentType(),
                request.sizeBytes(),
                request.storagePath(),
                request.checksum()));
    }

    /**
     * 查询文件上传记录。
     *
     * @param fileId 文件 ID
     * @return 上传记录
     */
    @Operation(summary = "查询文件上传记录", description = "按 fileId 查询文件元数据。")
    @GetMapping("/{fileId}")
    public ApiResponse<FileUploadRecord> getUpload(@PathVariable("fileId") String fileId) {
        log.info("收到文件上传查询请求, fileId={}", fileId);
        return RuntimeResponses.success(fileProcessingService.getUpload(fileId));
    }

    /**
     * 启动文件解析。
     *
     * @param fileId 文件 ID
     * @return 解析任务
     */
    @Operation(summary = "启动文件解析", description = "触发文件内容解析任务。")
    @PostMapping("/parse")
    public ApiResponse<FileParseTask> startParse(@RequestBody FileParseRequest request) {
        log.info("收到文件解析请求, fileId={}", request.fileId());
        return RuntimeResponses.success(fileProcessingService.startParse(request.fileId()));
    }

    /**
     * 查询最新解析结果。
     *
     * @param fileId 文件 ID
     * @return 解析任务
     */
    @Operation(summary = "查询文件解析结果", description = "按 fileId 获取最新解析结果。")
    @GetMapping("/{fileId}/parse-result")
    public ApiResponse<FileParseTask> getLatestParseResult(@PathVariable("fileId") String fileId) {
        log.info("收到文件解析结果查询请求, fileId={}", fileId);
        return RuntimeResponses.success(fileProcessingService.getLatestParseResult(fileId));
    }
}
