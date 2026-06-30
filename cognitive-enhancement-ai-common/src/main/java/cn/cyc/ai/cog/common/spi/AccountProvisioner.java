package cn.cyc.ai.cog.common.spi;

/**
 * 用户注册后自动开通商业账户（由 admin 模块实现，center 注册流程可选注入）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface AccountProvisioner {

    /**
     * 为 2C 个人用户创建 INDIVIDUAL 账户、默认会员与额度账户。
     *
     * @param userId      用户 ID
     * @param displayName 展示名（昵称或用户名）
     * @return 账户 ID
     */
    Long provisionIndividual(Long userId, String displayName);
}
