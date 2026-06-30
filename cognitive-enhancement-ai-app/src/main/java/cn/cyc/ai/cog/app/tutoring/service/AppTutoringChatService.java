package cn.cyc.ai.cog.app.tutoring.service;

import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatAcceptedResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatRequest;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringMessageVO;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStreamEvent;
import cn.cyc.ai.cog.app.tutoring.orchestrator.AppTutoringChatOrchestrator;
import cn.cyc.ai.cog.app.tutoring.support.AppTutoringStreamPublisher;
import cn.cyc.ai.cog.app.tutoring.support.AppTutoringTenantSync;
import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * C 端 AI 学习辅导对话门面服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppTutoringChatService {

    /**
     * 学习辅导对话编排器。
     */
    private final AppTutoringChatOrchestrator orchestrator;

    /**
     * SSE 流式事件发布器。
     */
    private final AppTutoringStreamPublisher streamPublisher;

    /**
     * 异步学习辅导任务执行器。
     */
    private final TaskExecutor appTutoringChatExecutor;

    /**
     * 创建学习辅导对话门面服务。
     *
     * @param orchestrator           对话编排器
     * @param streamPublisher        SSE 流式事件发布器
     * @param appTutoringChatExecutor 异步任务执行器
     */
    public AppTutoringChatService(AppTutoringChatOrchestrator orchestrator,
                                  AppTutoringStreamPublisher streamPublisher,
                                  @Qualifier("appTutoringChatExecutor") TaskExecutor appTutoringChatExecutor) {
        this.orchestrator = orchestrator;
        this.streamPublisher = streamPublisher;
        this.appTutoringChatExecutor = appTutoringChatExecutor;
    }

    /**
     * 同步发起学习辅导对话。
     *
     * @param request 对话请求
     * @return 对话响应
     */
    public AppTutoringChatResponse chat(AppTutoringChatRequest request) {
        return orchestrator.chat(request);
    }

    /**
     * 同步发起学习辅导对话并推送流式阶段事件。
     *
     * @param request       对话请求
     * @param eventConsumer 阶段事件消费者
     * @return 对话响应
     */
    public AppTutoringChatResponse chatStream(AppTutoringChatRequest request,
                                              Consumer<AppTutoringStreamEvent> eventConsumer) {
        return orchestrator.chatStream(request, eventConsumer);
    }

    /**
     * 异步触发学习辅导，阶段事件经 SSE 服务推送。
     *
     * @param request 对话请求
     * @return 受理响应，包含 traceId
     */
    public AppTutoringChatAcceptedResponse chatAsync(AppTutoringChatRequest request) {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            throw new ServiceException("未登录，无法发起学习辅导");
        }
        String traceId = UUID.randomUUID().toString();
        boolean includeUserWideChannel = !StringUtils.hasText(request.getSessionId());
        AuthUser user = UserContext.get();
        String tenantCode = TenantContext.currentTenantCode();
        Long tenantId = TenantContext.currentTenantId();

        appTutoringChatExecutor.execute(() -> runAsyncChat(
                request, traceId, userId, includeUserWideChannel, user, tenantCode, tenantId));

        AppTutoringChatAcceptedResponse accepted = new AppTutoringChatAcceptedResponse();
        accepted.setTraceId(traceId);
        accepted.setSessionId(request.getSessionId());
        return accepted;
    }

    /**
     * 查询指定会话的历史消息。
     *
     * @param sessionId 会话 ID
     * @return 消息列表
     */
    public List<AppTutoringMessageVO> listMessages(String sessionId) {
        return orchestrator.listMessages(sessionId);
    }

    /**
     * 在异步线程中执行学习辅导并发布 SSE 事件。
     *
     * @param request                  对话请求
     * @param traceId                  链路追踪 ID
     * @param userId                   用户 ID
     * @param includeUserWideChannel   是否发布到用户级 SSE 通道
     * @param user                     当前登录用户
     * @param tenantCode               租户编码
     * @param tenantId                 租户 ID
     */
    private void runAsyncChat(AppTutoringChatRequest request,
                              String traceId,
                              Long userId,
                              boolean includeUserWideChannel,
                              AuthUser user,
                              String tenantCode,
                              Long tenantId) {
        try {
            bindContext(user, tenantCode, tenantId);
            AppTutoringTenantSync.runWithRuntimeTenant(() ->
                    orchestrator.chatStream(request, traceId, event ->
                            streamPublisher.publish(userId, event, includeUserWideChannel)));
        } catch (Exception exception) {
            streamPublisher.publishFailed(userId, request, traceId, exception);
        } finally {
            clearContext();
        }
    }

    /**
     * 执行bind上下文。
     *
     * @param user 用户
     * @param tenantCode 租户编码
     * @param tenantId 租户 ID
     */
    private void bindContext(AuthUser user, String tenantCode, Long tenantId) {
        if (user != null) {
            UserContext.set(user);
        }
        if (StringUtils.hasText(tenantCode)) {
            TenantContext.setTenantCode(tenantCode);
        }
        if (tenantId != null) {
            TenantContext.setTenantId(tenantId);
        }
    }

    /**
     * 执行clear上下文。
     */
    private void clearContext() {
        UserContext.clear();
        TenantContext.clear();
        cn.cyc.ai.cog.runtime.security.TenantContext.clear();
    }
}
