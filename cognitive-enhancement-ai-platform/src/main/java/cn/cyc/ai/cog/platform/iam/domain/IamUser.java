package cn.cyc.ai.cog.platform.iam.domain;

/**
 * IAM 用户领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record IamUser(
        Long id,
        Long tenantId,
        String username,
        String nickname,
        String avatarUrl,
        String status,
        Long primaryAccountId,
        String email,
        String phone,
        String banReason,
        java.time.LocalDateTime banUntil,
        String userType,
        java.time.LocalDateTime createTime
) {
}
