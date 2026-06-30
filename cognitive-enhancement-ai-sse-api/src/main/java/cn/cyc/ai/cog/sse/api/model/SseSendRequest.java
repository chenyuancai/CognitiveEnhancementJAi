package cn.cyc.ai.cog.sse.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 服务间 SSE 推送请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class SseSendRequest {

    /**
     * 接收方连接键列表，格式见 {@link cn.cyc.ai.cog.sse.api.support.SseReceiverKey}。
     * <p>示例：仅 userId 写 {@code ["1"]}，并配合 {@code sessionId} 会自动展开为 {@code 1} 与 {@code 1:sessionId}；
     * 精确推送写 {@code ["1:your-session-id"]}。</p>
     */
    /** receiverKeys。 */
    @NotEmpty
    private List<String> receiverKeys;

    /**
     * SSE 事件名，如 CONTEXT_LOADED、ANSWER_DELTA、COMPLETED。
     */
    /** 事件名称。 */
    @NotBlank
    private String eventName;

    /** 链路 Trace ID */
    private String traceId;

    /** 会话 ID */
    private String sessionId;

    /**
     * 业务载荷，序列化后作为 event data 推送。
     */
    private Map<String, Object> payload;
}
