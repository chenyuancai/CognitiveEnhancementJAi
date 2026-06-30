package cn.cyc.ai.cog.sse.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.sse.api.SseConstants;
import cn.cyc.ai.cog.sse.api.model.SseSendRequest;
import cn.cyc.ai.cog.sse.service.SsePushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SSE 内部推送 API（供 app-server 等业务进程 Feign 调用）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "SSE-Inner", description = "服务间 SSE 推送")
@RestController
@RequestMapping(SseConstants.CLIENT_API_PREFIX)
@RequiredArgsConstructor
public class SseInnerController {

    /** ssePush服务。 */
    private final SsePushService ssePushService;

    /**
     * 执行send。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "推送 SSE 事件")
    @PostMapping("/send")
    public ApiResponse<Boolean> send(@Valid @RequestBody SseSendRequest request) {
        return ApiResponse.success(ssePushService.send(request));
    }
}
