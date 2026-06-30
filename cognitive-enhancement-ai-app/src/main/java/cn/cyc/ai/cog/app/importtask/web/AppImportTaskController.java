package cn.cyc.ai.cog.app.importtask.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.contract.AppPageQuery;
import cn.cyc.ai.cog.app.contract.AppSseJsonWriter;
import cn.cyc.ai.cog.app.importtask.dto.AppImportTaskCreateRequest;
import cn.cyc.ai.cog.app.importtask.service.AppImportTaskService;
import cn.cyc.ai.cog.app.importtask.support.AppImportProgressPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * C 端导入任务接口：任务创建、分页查询与进度 SSE 推送。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Tag(name = "App-导入", description = "导入任务与进度 SSE")
@RestController
@RequestMapping("/api/app/import-tasks")
public class AppImportTaskController {

    private final AppImportTaskService importTaskService;
    private final AppImportProgressPublisher progressPublisher;
    private final ObjectMapper objectMapper;

    public AppImportTaskController(AppImportTaskService importTaskService,
                                   AppImportProgressPublisher progressPublisher,
                                   ObjectMapper objectMapper) {
        this.importTaskService = importTaskService;
        this.progressPublisher = progressPublisher;
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "创建导入任务")
    @PostMapping
    public ApiResponse<?> create(@RequestBody AppImportTaskCreateRequest request) {
        return ApiResponse.success(importTaskService.create(request));
    }

    @Operation(summary = "导入任务分页")
    @PostMapping("/page")
    public ApiResponse<?> page(@RequestBody(required = false) AppPageQuery query) {
        return ApiResponse.success(importTaskService.page(query));
    }

    @Operation(summary = "导入任务详情")
    @GetMapping("/{id}")
    public ApiResponse<?> detail(@PathVariable String id) {
        return ApiResponse.success(importTaskService.detail(id));
    }

    @Operation(summary = "重试导入任务")
    @PostMapping("/{id}/retry")
    public ApiResponse<?> retry(@PathVariable String id) {
        return ApiResponse.success(importTaskService.retry(id));
    }

    @Operation(summary = "导入进度 SSE")
    @GetMapping(value = "/{id}/progress/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> progressStream(@PathVariable String id) {
        StreamingResponseBody body = outputStream -> progressPublisher.subscribe(id, event -> {
            try {
                AppSseJsonWriter.write(outputStream, objectMapper, event);
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        });
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "event-stream", StandardCharsets.UTF_8))
                .body(body);
    }
}
