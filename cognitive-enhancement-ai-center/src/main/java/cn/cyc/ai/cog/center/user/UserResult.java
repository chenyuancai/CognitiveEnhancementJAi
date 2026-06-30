package cn.cyc.ai.cog.center.user;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息返回结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
