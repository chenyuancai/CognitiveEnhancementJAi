package cn.cyc.ai.cog.center.user;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息返回结果。
 *
 * @param id           用户ID
 * @param username     用户名
 * @param tenantCode   租户编码
 * @param nickname     昵称
 * @param email        邮箱
 * @param phone        手机号
 * @param avatarUrl    头像URL
 * @param status       状态
 * @param roles        角色列表
 * @param lastLoginTime 最后登录时间
 * @param createTime   创建时间
 * @author cyc
 */
public record UserResult(
        Long id,
        String username,
        String tenantCode,
        String nickname,
        String email,
        String phone,
        String avatarUrl,
        String status,
        List<String> roles,
        LocalDateTime lastLoginTime,
        LocalDateTime createTime
) {
}
