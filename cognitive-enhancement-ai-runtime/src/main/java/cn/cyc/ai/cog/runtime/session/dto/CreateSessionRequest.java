package cn.cyc.ai.cog.runtime.session.dto;

/**
 * 创建会话请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CreateSessionRequest(
        String userId,
        String capabilityCode,
        String title
) {
}
