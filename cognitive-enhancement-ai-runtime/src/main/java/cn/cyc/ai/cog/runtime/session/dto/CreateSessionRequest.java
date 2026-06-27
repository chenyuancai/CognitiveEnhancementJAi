package cn.cyc.ai.cog.runtime.session.dto;

/**
 * 创建会话请求。
 *
 * @param userId         用户 ID
 * @param capabilityCode 能力编码
 * @param title          会话标题
 * @author cyc
 */
public record CreateSessionRequest(
        String userId,
        String capabilityCode,
        String title
) {
}
