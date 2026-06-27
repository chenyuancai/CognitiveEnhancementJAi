package cn.cyc.ai.cog.center.user;

/**
 * 用户登录请求。
 *
 * @param username 用户名
 * @param password 密码
 * @author cyc
 */
public record UserLoginRequest(
        String username,
        String password
) {
}
