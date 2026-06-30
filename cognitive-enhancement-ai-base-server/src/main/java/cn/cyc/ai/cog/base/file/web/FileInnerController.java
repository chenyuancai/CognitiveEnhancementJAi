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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "基础-文件(Inner)", description = "供其他服务 Feign 调用的文件接口")
@RestController
@RequestMapping(BaseFileConstants.INNER_API_PREFIX)
public class FileInnerController {

    /** 文件服务。 */
    private final FileService fileService;

    /**
     * 创建文件Inner接口。
     *
     * @param fileService 文件服务
     */
    public FileInnerController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 获取人ID。
     *
     * @param id 主键 ID
     * @return 人ID
     */
    @Operation(summary = "文件详情")
    @GetMapping("/{id}")
    public ApiResponse<FileInfoDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(fileService.getById(id));
    }

    /**
     * 执行downloadBytes。
     *
     * @param id 主键 ID
     * @return 统一错误响应
     */
    @Operation(summary = "下载文件字节（Feign）")
    @GetMapping("/{id}/bytes")
    public ResponseEntity<byte[]> downloadBytes(@PathVariable Long id) {
        byte[] bytes = fileService.readBytes(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    /**
     * 查询人Ids列表。
     *
     * @param ids ids
     * @return 结果列表
     */
    @Operation(summary = "批量查询文件")
    @GetMapping("/batch")
    public ApiResponse<List<FileInfoDTO>> listByIds(@RequestParam("ids") List<Long> ids) {
        return ApiResponse.success(fileService.listByIds(ids));
    }

    /**
     * 执行upload。
     * @return 执行结果
     */
    @Operation(summary = "Multipart 上传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileInfoDTO> upload(@RequestPart("file") MultipartFile file,
                                           @RequestParam("tenantId") Long tenantId,
                                           @RequestParam(value = "bizCode", required = false) String bizCode) {
        return ApiResponse.success(fileService.upload(file, tenantId, bizCode));
    }

    /**
     * 执行uploadBytes。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "Base64 字节上传")
    @PostMapping("/upload-bytes")
    public ApiResponse<FileInfoDTO> uploadBytes(@Valid @RequestBody FileUploadBytesRequest request) {
        return ApiResponse.success(fileService.uploadBytes(request));
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "文件分页")
    @PostMapping("/page")
    public ApiResponse<PageResult<FileInfoDTO>> page(@RequestBody FilePageQuery query) {
        return ApiResponse.success(fileService.page(query));
    }

    /**
     * 执行ensure。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "确认文件")
    @PostMapping("/ensure")
    public ApiResponse<Void> ensure(@Valid @RequestBody FileEnsureRequest request) {
        fileService.ensure(request);
        return ApiResponse.success(null);
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return ApiResponse.success(null);
    }
}
