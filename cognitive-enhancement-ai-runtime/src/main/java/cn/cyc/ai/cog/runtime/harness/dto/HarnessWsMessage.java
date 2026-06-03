package cn.cyc.ai.cog.runtime.harness.dto;

import java.util.Map;

/**
 * Harness WebSocket 消息统一结构。
 *
 * @param type    消息类型：CONNECTED / RUN / STEP / COMPLETE / ERROR / CANCEL
 * @param payload 消息载荷
 * @author cyc
 */
public record HarnessWsMessage(
        String type,
        Map<String, Object> payload
) {
}
