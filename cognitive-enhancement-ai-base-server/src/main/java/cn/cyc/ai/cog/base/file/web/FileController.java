package cn.cyc.ai.cog.base.file.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.base.api.file.FileEnsureRequest;
import cn.cyc.ai.cog.base.api.file.FileInfoDTO;
import cn.cyc.ai.cog.base.api.file.FilePageQuery;
import cn.cyc.ai.cog.base.file.service.FileService;
import cn.cyc.ai.cog.common.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件服务对外 HTTP API（浏览器 / 网关直连）。
 */
@Tag(name = "基础-文件", description = "Multipart 上传、下载、预览")
@RestController
@RequestMapping("/api/base/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "Multipart 上传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileInfoDTO> upload(@RequestPart("file") MultipartFile file,
                                           @RequestParam("tenantId") Long tenantId,
                                           @RequestParam(value = "bizCode", required = false) String bizCode) {
        return ApiResponse.success(fileService.upload(file, tenantId, bizCode));
    }

    @Operation(summary = "文件详情")
    @GetMapping("/{id}")
    public ApiResponse<FileInfoDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(fileService.getById(id));
    }

    @Operation(summary = "文件分页")
    @PostMapping("/page")
    public ApiResponse<PageResult<FileInfoDTO>> page(@RequestBody FilePageQuery query) {
        return ApiResponse.success(fileService.page(query));
    }

    @Operation(summary = "确认文件")
    @PostMapping("/ensure")
    public ApiResponse<Void> ensure(@Valid @RequestBody FileEnsureRequest request) {
        fileService.ensure(request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return ApiResponse.success(null);
    }

    @Operation(summary = "下载文件")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        return buildFileResponse(id, true);
    }

    @Operation(summary = "预览文件")
    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> preview(@PathVariable Long id) {
        return buildFileResponse(id, false);
    }

    private ResponseEntity<Resource> buildFileResponse(Long id, boolean attachment) {
        Resource resource = fileService.loadAsResource(id);
        String fileName = fileService.resolveOriginalName(id);
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        String disposition = attachment ? "attachment" : "inline";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileService.resolveContentType(id)))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename*=UTF-8''" + encoded)
                .body(resource);
    }
}
