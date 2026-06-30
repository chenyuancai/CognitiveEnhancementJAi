package cn.cyc.ai.cog.sse.service;

import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.sse.api.model.SseSendRequest;
import cn.cyc.ai.cog.sse.api.support.SseReceiverKey;
import cn.cyc.ai.cog.sse.support.SseConnectionManager;
import cn.cyc.ai.cog.sse.support.SseReceiverKeyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

/**
 * SSE 连接与推送编排。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
@RequiredArgsConstructor
public class SsePushService {

    /** connectionManager。 */
    private final SseConnectionManager connectionManager;

    /**
     * 执行connect。
     *
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    public SseEmitter connect(String sessionId) {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            throw new ServiceException("未登录，无法建立 SSE 连接");
        }
        return connectionManager.connect(SseReceiverKey.of(userId, sessionId));
    }

    /**
     * 执行disconnect。
     *
     * @param sessionId 会话 ID
     */
    public void disconnect(String sessionId) {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            return;
        }
        connectionManager.disconnect(SseReceiverKey.of(userId, sessionId));
    }

    /**
     * 执行send。
     *
     * @param request 请求
     * @return 执行结果
     */
    public boolean send(SseSendRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getReceiverKeys())) {
            return false;
        }
        Map<String, Object> envelope = buildEnvelope(request);
        boolean delivered = false;
        for (String receiverKey : SseReceiverKeyResolver.resolve(request.getReceiverKeys(), request.getSessionId())) {
            delivered = connectionManager.send(receiverKey, request.getEventName(), envelope) || delivered;
        }
        return delivered;
    }

    private Map<String, Object> buildEnvelope(SseSendRequest request) {
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", request.getEventName());
        if (StringUtils.hasText(request.getTraceId())) {
            envelope.put("traceId", request.getTraceId());
        }
        if (StringUtils.hasText(request.getSessionId())) {
            envelope.put("sessionId", request.getSessionId());
        }
        if (request.getPayload() != null) {
            envelope.put("payload", request.getPayload());
        }
        return envelope;
    }
}
