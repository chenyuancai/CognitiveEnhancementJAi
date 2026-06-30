package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.dto.AppLearningInvokeRequest;
import cn.cyc.ai.cog.app.dto.AppLearningModesVO;
import cn.cyc.ai.cog.app.service.AppLearningService;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * C 端学习链路接口：评分 / 带学 / 问答。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-学习链路", description = "按会员权益调用 AI 能力（委托 Runtime）")
@RestController
@RequestMapping("/api/app/learning")
public class AppLearningController {

    /** C 端学习服务 */
    private final AppLearningService appLearningService;

    /**
     * @param appLearningService C 端学习服务
     */
    public AppLearningController(AppLearningService appLearningService) {
        this.appLearningService = appLearningService;
    }

    /**
     * 执行modes。
     * @return 执行结果
     */
    @Operation(summary = "可用学习模式")
    @GetMapping("/modes")
    public ApiResponse<AppLearningModesVO> modes() {
        return ApiResponse.success(appLearningService.listModes());
    }

    /**
     * 执行操作。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "调用学习 AI 能力", description = "mode=SCORING|TUTORING|QA")
    @PostMapping("/invoke")
    public ApiResponse<CapabilityExecuteResponse> invoke(@Valid @RequestBody AppLearningInvokeRequest request) {
        return ApiResponse.success(appLearningService.invoke(request));
    }

    @Operation(summary = "流式调用学习 AI 能力", description = "TUTORING 已迁移至 /api/app/tutoring/chat/stream")
    @PostMapping(value = "/invoke/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> invokeStream(@Valid @RequestBody AppLearningInvokeRequest request) {
        if (!"TUTORING".equalsIgnoreCase(request.getMode())) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "请使用 /api/app/tutoring/chat/stream");
        }
        StreamingResponseBody body = outputStream -> {
            Map<String, Object> frame = Map.of(
                    "type", "error",
                    "message", "请使用 /api/app/tutoring/chat/stream；learning/invoke/stream 已废弃");
            outputStream.write(("data: " + new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(frame) + "\n\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        };
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "event-stream", StandardCharsets.UTF_8))
                .body(body);
    }
}
