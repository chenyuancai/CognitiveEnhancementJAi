package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.base.api.file.FileInfoDTO;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.platform.file.spi.PlatformFileClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * C 端文件上传（委托 base-server）。
 */
@Tag(name = "App-文件", description = "C 端文件上传")
@RestController
@RequestMapping("/api/app/files")
public class AppFileController {

    private static final String DEFAULT_BIZ_CODE = "app";

    private final PlatformFileClient platformFileClient;

    public AppFileController(PlatformFileClient platformFileClient) {
        this.platformFileClient = platformFileClient;
    }

    @Operation(summary = "Multipart 上传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileInfoDTO> upload(@RequestPart("file") MultipartFile file,
                                           @RequestParam(value = "bizCode", required = false) String bizCode) {
        Long tenantId = TenantContext.currentTenantId();
        String resolvedBizCode = bizCode != null && !bizCode.isBlank() ? bizCode.trim() : DEFAULT_BIZ_CODE;
        return ApiResponse.success(platformFileClient.uploadMultipart(tenantId, resolvedBizCode, file));
    }
}
