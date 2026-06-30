package cn.cyc.ai.cog.runtime.web;

import cn.cyc.ai.cog.api.enums.ErrorCode;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.core.harness.RuntimeHarness;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Runtime 能力入口控制器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Runtime - 能力执行", description = "能力运行时入口：同步执行与 SSE 流式执行")
@RestController
@RequestMapping("/api/runtime/capabilities")
public class CapabilityRuntimeController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(CapabilityRuntimeController.class);

    /**
     * 运行时治理器。
     */
    private final RuntimeHarness runtimeHarness;

    /**
     * JSON 序列化器。
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造能力运行时控制器。
     *
     * @param runtimeHarness 运行时治理器
     * @param objectMapper   JSON 序列化器
     */
    public CapabilityRuntimeController(RuntimeHarness runtimeHarness, ObjectMapper objectMapper) {
        this.runtimeHarness = runtimeHarness;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行能力主链路。
     *
     * @param request 执行请求
     * @return 执行响应
     */
    @Operation(summary = "同步执行能力", description = "走 Capability→Agent→Tool/LLM 主链路。parameters 可透传 PH5 治理开关（planningEnabled、reflectionEnabled 等）。")
    @PostMapping("/execute")
    public ApiResponse<CapabilityExecuteResponse> execute(@RequestBody CapabilityExecuteRequest request) {
        log.info("收到 Runtime API 请求, capabilityCode={}", request.capabilityCode());
        return RuntimeResponses.success(runtimeHarness.execute(request));
    }

    /**
     * 以 SSE 事件流执行能力。
     *
     * @param request 执行请求
     * @return SSE 响应体
     */
    @Operation(summary = "流式执行能力（SSE）", description = "以 text/event-stream 推送 STARTED/COMPLETED/FAILED 事件，业务语义与同步接口一致。")
    @PostMapping(value = "/execute/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> executeStream(@RequestBody CapabilityExecuteRequest request) {
        String traceId = TraceContext.getTraceId();
        log.info("收到 Runtime SSE 请求, traceId={}, capabilityCode={}", traceId, request.capabilityCode());
        StreamingResponseBody body = outputStream -> streamExecution(outputStream, traceId, request);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "event-stream", StandardCharsets.UTF_8))
                .body(body);
    }

    /**
     * 执行流Execution。
     *
     * @param outputStream 输出流
     * @param traceId 链路 Trace ID
     * @param request 请求
     */
    private void streamExecution(OutputStream outputStream, String traceId, CapabilityExecuteRequest request)
            throws IOException {
        TraceContext.setTraceId(traceId);
        try {
            writeEvent(outputStream, "STARTED", CapabilityExecutionStreamEvent.started(traceId, request.capabilityCode()));
            CapabilityExecuteResponse response = runtimeHarness.execute(request);
            writeEvent(outputStream, "COMPLETED", CapabilityExecutionStreamEvent.completed(
                    response.traceId(), request.capabilityCode(), response));
        } catch (RuntimeException ex) {
            ErrorCode errorCode = mapErrorCode(ex);
            log.warn("Runtime SSE 执行失败, traceId={}, capabilityCode={}, code={}, message={}",
                    traceId, request.capabilityCode(), errorCode.getCode(), ex.getMessage());
            writeEvent(outputStream, "FAILED", CapabilityExecutionStreamEvent.failed(
                    traceId, request.capabilityCode(), errorCode, ex.getMessage()));
        } finally {
            TraceContext.clear();
        }
    }

    /**
     * 执行write事件。
     *
     * @param outputStream 输出流
     * @param eventName 事件名称
     * @param event 事件
     */
    private void writeEvent(OutputStream outputStream, String eventName, CapabilityExecutionStreamEvent event)
            throws IOException {
        outputStream.write(("event: " + eventName + "\n").getBytes(StandardCharsets.UTF_8));
        outputStream.write(("data: " + objectMapper.writeValueAsString(event) + "\n\n").getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    /**
     * 执行map错误编码。
     *
     * @param exception exception
     * @return 执行结果
     */
    private ErrorCode mapErrorCode(RuntimeException exception) {
        if (exception instanceof ServiceException serviceException && serviceException.getCategory() != null) {
            return serviceException.getCategory();
        }
        return ErrorCode.SYSTEM_ERROR;
    }

    /**
     * 能力Execution流事件
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    private record CapabilityExecutionStreamEvent(
            String type,
            String traceId,
            String capabilityCode,
            boolean success,
            String code,
            String message,
            CapabilityExecuteResponse data
    ) {

        /**
         * 执行started。
         *
         * @param traceId 链路 Trace ID
         * @param capabilityCode 能力编码
         * @return 执行结果
         */
        private static CapabilityExecutionStreamEvent started(String traceId, String capabilityCode) {
            return new CapabilityExecutionStreamEvent(
                    "STARTED",
                    traceId,
                    capabilityCode,
                    true,
                    ErrorCode.SUCCESS.getCode(),
                    "能力执行已开始",
                    null
            );
        }

        /**
         * 执行completed。
         * @return 执行结果
         */
        private static CapabilityExecutionStreamEvent completed(String traceId,
                                                                String capabilityCode,
                                                                CapabilityExecuteResponse data) {
            return new CapabilityExecutionStreamEvent(
                    "COMPLETED",
                    traceId,
                    capabilityCode,
                    true,
                    ErrorCode.SUCCESS.getCode(),
                    ErrorCode.SUCCESS.getMessage(),
                    data
            );
        }

        /**
         * 执行failed。
         * @return 执行结果
         */
        private static CapabilityExecutionStreamEvent failed(String traceId,
                                                             String capabilityCode,
                                                             ErrorCode errorCode,
                                                             String message) {
            return new CapabilityExecutionStreamEvent(
                    "FAILED",
                    traceId,
                    capabilityCode,
                    false,
                    errorCode.getCode(),
                    message,
                    null
            );
        }
    }
}
