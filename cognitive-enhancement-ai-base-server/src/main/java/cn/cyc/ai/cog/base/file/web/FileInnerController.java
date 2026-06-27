package cn.cyc.ai.cog.base.file.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.base.api.file.FileEnsureRequest;
import cn.cyc.ai.cog.base.api.file.FileInfoDTO;
import cn.cyc.ai.cog.base.api.file.FilePageQuery;
import cn.cyc.ai.cog.base.api.file.FileUploadBytesRequest;
import cn.cyc.ai.cog.base.api.file.BaseFileConstants;
import cn.cyc.ai.cog.base.file.service.FileService;
import cn.cyc.ai.cog.common.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
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

import java.util.List;

/**
 * 文件服务内部 API（Feign 与微服务间调用）。
 */
@Tag(name = "基础-文件(Inner)", description = "供其他服务 Feign 调用的文件接口")
@RestController
@RequestMapping(BaseFileConstants.INNER_API_PREFIX)
public class FileInnerController {

    private final FileService fileService;

    public FileInnerController(FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "文件详情")
    @GetMapping("/{id}")
    public ApiResponse<FileInfoDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(fileService.getById(id));
    }

    @Operation(summary = "下载文件字节（Feign）")
    @GetMapping("/{id}/bytes")
    public ResponseEntity<byte[]> downloadBytes(@PathVariable Long id) {
        byte[] bytes = fileService.readBytes(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @Operation(summary = "批量查询文件")
    @GetMapping("/batch")
    public ApiResponse<List<FileInfoDTO>> listByIds(@RequestParam("ids") List<Long> ids) {
        return ApiResponse.success(fileService.listByIds(ids));
    }

    @Operation(summary = "Multipart 上传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileInfoDTO> upload(@RequestPart("file") MultipartFile file,
                                           @RequestParam("tenantId") Long tenantId,
                                           @RequestParam(value = "bizCode", required = false) String bizCode) {
        return ApiResponse.success(fileService.upload(file, tenantId, bizCode));
    }

    @Operation(summary = "Base64 字节上传")
    @PostMapping("/upload-bytes")
    public ApiResponse<FileInfoDTO> uploadBytes(@Valid @RequestBody FileUploadBytesRequest request) {
        return ApiResponse.success(fileService.uploadBytes(request));
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
}
