package cn.cyc.ai.cog.app.tutoring.support;

import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatRequest;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStreamEvent;

/**
 * 学习辅导阶段事件推送 SPI，默认经独立 SSE 服务分发。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface AppTutoringStreamPublisher {

    /**
     * 推送阶段事件到 SSE 服务。
     *
     * @param userId                 接收用户 ID
     * @param event                  阶段事件
     * @param includeUserWideChannel 是否同时推送到 userId 级连接（新会话尚未带 sessionId 建连时使用）
     */
    void publish(Long userId, AppTutoringStreamEvent event, boolean includeUserWideChannel);

    /**
     * 推送失败事件到 SSE 服务。
     *
     * @param userId  接收用户 ID
     * @param request 原始聊天请求
     * @param traceId 追踪 ID
     * @param error   失败异常
     */
    default void publishFailed(Long userId, AppTutoringChatRequest request, String traceId, Throwable error) {
        String message = error.getMessage() == null ? "学习辅导执行失败" : error.getMessage();
        publish(userId, AppTutoringStreamEvent.of(
                "FAILED",
                traceId,
                request.getSessionId(),
                java.util.Map.of("message", message)),
                !org.springframework.util.StringUtils.hasText(request.getSessionId()));
    }
}
