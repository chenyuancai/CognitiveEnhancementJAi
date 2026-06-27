package cn.cyc.ai.cog.center.user;

/**
 * 用户注册请求。
 *
 * @param username 用户名
 * @param password 密码
 * @param nickname 昵称
 * @param email    邮箱
 * @param phone    手机号
 * @author cyc
 */
public record UserRegisterRequest(
        String username,
        String password,
        String nickname,
        String email,
        String phone
) {
}
