package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

import java.util.Map;

/**
 * 学习辅导 SSE 阶段事件。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringStreamEvent {

    /** 事件类型。 */
    private String type;

    /** 链路 Trace ID。 */
    private String traceId;

    /** 会话 ID。 */
    private String sessionId;

    /** 事件负载数据。 */
    private Map<String, Object> payload;

    /**
     * 构建流式事件对象。
     *
     * @param type      事件类型
     * @param traceId   链路 Trace ID
     * @param sessionId 会话 ID
     * @param payload   事件负载
     * @return 流式事件实例
     */
    public static AppTutoringStreamEvent of(String type, String traceId, String sessionId, Map<String, Object> payload) {
        AppTutoringStreamEvent event = new AppTutoringStreamEvent();
        event.setType(type);
        event.setTraceId(traceId);
        event.setSessionId(sessionId);
        event.setPayload(payload);
        return event;
    }
}
