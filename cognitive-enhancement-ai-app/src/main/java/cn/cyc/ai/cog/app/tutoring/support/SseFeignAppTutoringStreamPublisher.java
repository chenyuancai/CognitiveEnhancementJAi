package cn.cyc.ai.cog.app.tutoring.support;

import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStreamEvent;
import cn.cyc.ai.cog.sse.api.SseFeignClient;
import cn.cyc.ai.cog.sse.api.model.SseSendRequest;
import cn.cyc.ai.cog.sse.api.support.SseReceiverKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 通过 Feign 将辅导阶段事件推送到 SSE 单副本服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Slf4j
public class SseFeignAppTutoringStreamPublisher implements AppTutoringStreamPublisher {

    /** SSE Feign 客户端。 */
    private final SseFeignClient sseFeignClient;

    /**
     * 构造 Feign SSE 推送器。
     *
     * @param sseFeignClient SSE Feign 客户端
     */
    public SseFeignAppTutoringStreamPublisher(SseFeignClient sseFeignClient) {
        this.sseFeignClient = sseFeignClient;
    }

    /**
     * {@inheritDoc}
     */
    /**
     * 执行publish。
     *
     * @param userId 用户 ID
     * @param event 事件
     * @param includeUserWideChannel include用户WideChannel
     */
    @Override
    public void publish(Long userId, AppTutoringStreamEvent event, boolean includeUserWideChannel) {
        if (userId == null || event == null) {
            return;
        }
        List<String> receiverKeys = resolveReceiverKeys(userId, event.getSessionId(), includeUserWideChannel);
        if (receiverKeys.isEmpty()) {
            return;
        }
        SseSendRequest request = new SseSendRequest();
        request.setReceiverKeys(receiverKeys);
        request.setEventName(event.getType());
        request.setTraceId(event.getTraceId());
        request.setSessionId(event.getSessionId());
        request.setPayload(buildPayload(event));
        try {
            sseFeignClient.send(request);
        } catch (Exception exception) {
            log.warn("SSE 推送失败: userId={}, event={}, message={}",
                    userId, event.getType(), exception.getMessage());
        }
    }

    /**
     * 解析 SSE 接收方键列表。
     *
     * @param userId                 用户 ID
     * @param sessionId              会话 ID
     * @param includeUserWideChannel 是否包含用户级广播通道
     * @return 接收方键列表
     */
    private List<String> resolveReceiverKeys(Long userId, String sessionId, boolean includeUserWideChannel) {
        Set<String> keys = new LinkedHashSet<>();
        keys.add(SseReceiverKey.of(userId, null));
        if (StringUtils.hasText(sessionId)) {
            keys.add(SseReceiverKey.of(userId, sessionId));
        }
        return new ArrayList<>(keys);
    }

    /**
     * 构建 SSE 事件负载。
     *
     * @param event 阶段事件
     * @return 事件负载映射
     */
    private Map<String, Object> buildPayload(AppTutoringStreamEvent event) {
        if (event.getPayload() == null) {
            return Map.of();
        }
        return event.getPayload();
    }
}
