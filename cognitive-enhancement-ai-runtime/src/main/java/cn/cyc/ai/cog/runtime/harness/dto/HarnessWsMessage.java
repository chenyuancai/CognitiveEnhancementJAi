package cn.cyc.ai.cog.runtime.harness.dto;

import java.util.Map;

/**
 * Harness WebSocket 消息统一结构。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record HarnessWsMessage(
        String type,
        Map<String, Object> payload
) {
}
