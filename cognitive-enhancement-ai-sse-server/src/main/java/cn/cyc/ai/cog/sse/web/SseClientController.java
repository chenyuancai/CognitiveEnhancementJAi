package cn.cyc.ai.cog.sse.web;

import cn.cyc.ai.cog.sse.api.SseConstants;
import cn.cyc.ai.cog.sse.service.SsePushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 客户端连接 API（前端经网关访问，单副本持有长连接）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "SSE-客户端", description = "建立/断开 SSE 长连接")
@RestController
@RequestMapping(SseConstants.CLIENT_API_PREFIX)
@RequiredArgsConstructor
public class SseClientController {

    /** ssePush服务。 */
    private final SsePushService ssePushService;

    /**
     * 执行connect。
     *
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    @Operation(summary = "建立 SSE 连接", description = "连接键为 userId 或 userId:sessionId，需登录态")
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam(value = "sessionId", required = false) String sessionId) {
        return ssePushService.connect(sessionId);
    }

    /**
     * 执行disconnect。
     *
     * @param sessionId 会话 ID
     */
    @Operation(summary = "断开 SSE 连接")
    @PostMapping("/disconnect")
    @SecurityRequirements
    public void disconnect(@RequestParam(value = "sessionId", required = false) String sessionId) {
        ssePushService.disconnect(sessionId);
    }
}
