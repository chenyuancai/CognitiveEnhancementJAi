package cn.cyc.ai.cog.sse.api.support;

import org.springframework.util.StringUtils;

/**
 * SSE 接收方连接键：{@code userId} 或 {@code userId:sessionId}。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class SseReceiverKey {

    /**
     * 创建SseReceiverKey。
     */
    private SseReceiverKey() {
    }

    /**
     * 执行of。
     *
     * @param userId 用户 ID
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    public static String of(Long userId, String sessionId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId 不能为空");
        }
        if (!StringUtils.hasText(sessionId)) {
            return String.valueOf(userId);
        }
        return userId + ":" + sessionId.trim();
    }

    /**
     * 执行of。
     *
     * @param userId 用户 ID
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    public static String of(String userId, String sessionId) {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("userId 不能为空");
        }
        if (!StringUtils.hasText(sessionId)) {
            return userId.trim();
        }
        return userId.trim() + ":" + sessionId.trim();
    }
}
