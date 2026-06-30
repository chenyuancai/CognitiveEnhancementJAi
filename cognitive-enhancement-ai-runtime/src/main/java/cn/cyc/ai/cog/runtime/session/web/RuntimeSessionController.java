package cn.cyc.ai.cog.runtime.session.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;
import cn.cyc.ai.cog.runtime.session.dto.CreateSessionRequest;
import cn.cyc.ai.cog.runtime.session.service.ConversationSessionService;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime 会话接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Runtime - 会话", description = "多轮会话上下文管理")
@RestController
@RequestMapping("/api/runtime/sessions")
public class RuntimeSessionController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(RuntimeSessionController.class);

    /**
     * 会话服务。
     */
    private final ConversationSessionService conversationSessionService;

    /**
     * 构造 Runtime 会话接口。
     *
     * @param conversationSessionService 会话服务
     */
    public RuntimeSessionController(ConversationSessionService conversationSessionService) {
        this.conversationSessionService = conversationSessionService;
    }

    /**
     * 创建会话。
     *
     * @param request 创建会话请求
     * @return 新建会话
     */
    @Operation(summary = "创建会话", description = "为指定用户与能力创建多轮会话，返回新会话元数据。")
    @PostMapping
    public ApiResponse<ConversationSession> createSession(@RequestBody CreateSessionRequest request) {
        log.info("收到创建会话请求, userId={}, capabilityCode={}", request.userId(), request.capabilityCode());
        return RuntimeResponses.success(conversationSessionService.createSession(
                request.userId(), request.capabilityCode(), request.title()));
    }

    /**
     * 查询会话详情。
     *
     * @param sessionId 会话 ID
     * @return 会话详情
     */
    @Operation(summary = "查询会话详情", description = "按 sessionId 查询会话元数据。")
    @GetMapping("/{sessionId}")
    public ApiResponse<ConversationSession> getSession(@PathVariable String sessionId) {
        log.info("收到会话详情查询请求, sessionId={}", sessionId);
        return RuntimeResponses.success(conversationSessionService.getSession(sessionId));
    }

    /**
     * 查询会话消息列表。
     *
     * @param sessionId 会话 ID
     * @return 消息列表
     */
    @Operation(summary = "查询会话消息", description = "按 sessionId 分页查询历史消息。")
    @GetMapping("/{sessionId}/messages")
    public ApiResponse<RuntimeListResult<ConversationMessage>> listMessages(@PathVariable String sessionId) {
        log.info("收到会话消息查询请求, sessionId={}", sessionId);
        var messages = conversationSessionService.listMessages(sessionId);
        return RuntimeResponses.success(new RuntimeListResult<>(messages.size(), messages));
    }
}
