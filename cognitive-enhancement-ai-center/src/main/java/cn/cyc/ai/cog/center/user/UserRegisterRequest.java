package cn.cyc.ai.cog.center.user;

/**
 * 用户注册请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record UserRegisterRequest(
        String username,
        String password,
        String nickname,
        String email,
        String phone
) {
}
