package cn.cyc.ai.cog.sse.support;

import cn.cyc.ai.cog.sse.api.support.SseReceiverKey;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 将推送请求中的 receiverKeys 规范化为连接表实际使用的键。
 */
public final class SseReceiverKeyResolver {

    private SseReceiverKeyResolver() {
    }

    /**
     * 展开接收方键：纯 userId 会同时映射到用户级与 session 级通道。
     *
     * @param receiverKeys 原始键列表（可为 {@code "1"} 或 {@code "1:session-1"}）
     * @param sessionId    请求中的会话 ID（可选）
     * @return 去重后的连接键
     */
    public static List<String> resolve(List<String> receiverKeys, String sessionId) {
        if (receiverKeys == null || receiverKeys.isEmpty()) {
            return List.of();
        }
        Set<String> resolved = new LinkedHashSet<>();
        for (String raw : receiverKeys) {
            if (!StringUtils.hasText(raw)) {
                continue;
            }
            String key = raw.trim();
            int colonIndex = key.indexOf(':');
            if (colonIndex > 0) {
                resolved.add(key);
                continue;
            }
            Long userId = parseUserId(key);
            if (userId == null) {
                resolved.add(key);
                continue;
            }
            resolved.add(SseReceiverKey.of(userId, null));
            String effectiveSessionId = StringUtils.hasText(sessionId) ? sessionId : null;
            if (StringUtils.hasText(effectiveSessionId)) {
                resolved.add(SseReceiverKey.of(userId, effectiveSessionId));
            }
        }
        return new ArrayList<>(resolved);
    }

    private static Long parseUserId(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
