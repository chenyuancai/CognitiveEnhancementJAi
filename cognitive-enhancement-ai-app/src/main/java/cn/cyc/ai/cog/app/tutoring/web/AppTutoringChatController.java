package cn.cyc.ai.cog.app.tutoring.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatAcceptedResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatRequest;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringMessageVO;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStreamEvent;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringChatService;
import cn.cyc.ai.cog.app.tutoring.support.AppTutoringSseAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

/**
 * C 端学习辅导对话接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-学习辅导", description = "学习辅导型 AI 对话")
@RestController
@RequestMapping("/api/app/tutoring")
public class AppTutoringChatController {

    /**
     * 学习辅导对话服务。
     */
    private final AppTutoringChatService appTutoringChatService;

    /**
     * JSON 序列化器。
     */
    private final ObjectMapper objectMapper;

    /**
     * 创建学习辅导对话控制器。
     *
     * @param appTutoringChatService 学习辅导对话服务
     * @param objectMapper           JSON 序列化器
     */
    public AppTutoringChatController(AppTutoringChatService appTutoringChatService,
                                     ObjectMapper objectMapper) {
        this.appTutoringChatService = appTutoringChatService;
        this.objectMapper = objectMapper;
    }

    /**
     * 同步发起学习辅导对话。
     *
     * @param request 对话请求
     * @return 对话响应
     */
    /**
     * 执行chat。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "学习辅导对话", description = "自动创建或复用会话，保存聊天记录并按教学策略生成回复")
    @PostMapping("/chat")
    public ApiResponse<AppTutoringChatResponse> chat(@Valid @RequestBody AppTutoringChatRequest request) {
        return ApiResponse.success(appTutoringChatService.chat(request));
    }

    /**
     * 同步发起学习辅导对话并以 SSE 推送阶段事件。
     *
     * @param request 对话请求
     * @return SSE 流式响应
     */
    /**
     * 执行chat流。
     *
     * @param request 请求
     * @return 统一错误响应
     */
    @Operation(
            summary = "学习辅导对话（同步 SSE）",
            description = "以 text/event-stream 推送 PROFILE_LOADED / CONTEXT_LOADED / STRATEGY_SELECTED / "
                    + "BLUEPRINT_READY / GOVERNANCE_APPLIED / COMPLETED 阶段事件")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> chatStream(@Valid @RequestBody AppTutoringChatRequest request) {
        StreamingResponseBody body = outputStream -> appTutoringChatService.chatStream(request, event -> {
            try {
                writeEvent(outputStream, event);
            } catch (Exception ex) {
                throw new IllegalStateException("tutoring stream write failed", ex);
            }
        });
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "event-stream", StandardCharsets.UTF_8))
                .body(body);
    }

    /**
     * 异步发起学习辅导对话，阶段事件经独立 SSE 通道推送。
     *
     * @param request 对话请求
     * @return 受理响应，包含 traceId
     */
    /**
     * 执行chatAsync。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(
            summary = "学习辅导对话（异步 + SSE）",
            description = "立即返回 traceId；前端需先 GET /api/sse/connect 建连，阶段事件经 SSE 服务推送："
                    + "PROFILE_LOADED / CONTEXT_LOADED / STRATEGY_SELECTED / BLUEPRINT_READY / "
                    + "GOVERNANCE_APPLIED / COMPLETED（失败 FAILED）")
    @PostMapping("/chat/async")
    public ApiResponse<AppTutoringChatAcceptedResponse> chatAsync(@Valid @RequestBody AppTutoringChatRequest request) {
        return ApiResponse.success(appTutoringChatService.chatAsync(request));
    }

    /**
     * 查询指定会话的历史消息。
     *
     * @param sessionId 会话 ID
     * @return 消息列表
     */
    /**
     * 执行messages。
     *
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    @Operation(summary = "查询学习辅导会话消息", description = "按会话 ID 查询当前用户可访问的历史消息")
    @GetMapping("/sessions/{sessionId}/messages")
    public ApiResponse<java.util.List<AppTutoringMessageVO>> messages(@PathVariable String sessionId) {
        return ApiResponse.success(appTutoringChatService.listMessages(sessionId));
    }

    /**
     * 将流式阶段事件写入 SSE 输出流。
     *
     * @param outputStream 响应输出流
     * @param event        阶段事件
     * @throws java.io.IOException 写入失败时抛出
     */
    private void writeEvent(java.io.OutputStream outputStream, AppTutoringStreamEvent event) throws java.io.IOException {
        outputStream.write(("data: " + objectMapper.writeValueAsString(AppTutoringSseAdapter.toContractEvent(event)) + "\n\n")
                .getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
