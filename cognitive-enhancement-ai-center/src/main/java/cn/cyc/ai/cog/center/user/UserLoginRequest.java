package cn.cyc.ai.cog.center.user;

/**
 * 用户登录请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record UserLoginRequest(
        String username,
        String password
) {
}
