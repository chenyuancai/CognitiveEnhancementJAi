package cn.cyc.ai.cog.base.api.file;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 基础文件服务 Feign 客户端（供 admin / app / runtime 等进程调用）。
 *
 * <p>默认直连 {@code cog.base.file.url}，生产可改为服务发现。</p>
 */
@FeignClient(
        name = BaseFileConstants.SERVICE_NAME,
        url = "${cog.base.file.url:http://localhost:8805}",
        path = BaseFileConstants.INNER_API_PREFIX
)
public interface BaseFileFeignClient {

    @GetMapping("/{id}")
    ApiResponse<FileInfoDTO> getById(@PathVariable("id") Long id);

    @GetMapping("/{id}/bytes")
    ResponseEntity<byte[]> downloadBytes(@PathVariable("id") Long id);

    @GetMapping("/batch")
    ApiResponse<List<FileInfoDTO>> listByIds(@RequestParam("ids") List<Long> ids);

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<FileInfoDTO> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("tenantId") Long tenantId,
            @RequestParam(value = "bizCode", required = false) String bizCode);

    @PostMapping("/upload-bytes")
    ApiResponse<FileInfoDTO> uploadBytes(@Valid @RequestBody FileUploadBytesRequest request);

    @PostMapping("/page")
    ApiResponse<PageResult<FileInfoDTO>> page(@RequestBody FilePageQuery query);

    @PostMapping("/ensure")
    ApiResponse<Void> ensure(@Valid @RequestBody FileEnsureRequest request);

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable("id") Long id);
}
