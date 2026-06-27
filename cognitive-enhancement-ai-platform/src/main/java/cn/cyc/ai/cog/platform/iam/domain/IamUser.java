package cn.cyc.ai.cog.platform.iam.domain;

/**
 * IAM 用户领域对象。
 *
 * @param id                用户 ID
 * @param tenantId          租户 ID
 * @param username          登录名
 * @param nickname          昵称
 * @param avatarUrl         头像 URL
 * @param status            状态
 * @param primaryAccountId  主账户 ID
 * @param email             邮箱
 * @param phone             手机号
 * @param banReason         封禁原因
 * @param banUntil          封禁截止时间
 * @param userType          用户类型（ADMIN/CUSTOMER）
 * @param createTime        创建时间
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
