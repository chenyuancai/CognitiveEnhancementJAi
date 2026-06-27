package cn.cyc.ai.cog.platform.quota.repository;

import cn.cyc.ai.cog.platform.quota.domain.QuotaAccount;

/**
 * 额度账户仓储接口。
 */
public interface QuotaAccountRepository {

    /**
     * 按账户 ID 查询额度账户，不存在时返回 null。
     *
     * @param accountId 商业账户 ID
     * @return 额度账户或 null
     */
    QuotaAccount findByAccountId(Long accountId);

    /**
     * 按账户 ID 查询额度账户，不存在时抛出异常。
     *
     * @param accountId 商业账户 ID
     * @return 额度账户
     */
    QuotaAccount requireByAccountId(Long accountId);

    /**
     * 开户时初始化额度账户。
     *
     * @param tenantId   租户 ID
     * @param accountId  商业账户 ID
     * @param cycleTotal 周期额度总量
     */
    void insertInitial(Long tenantId, Long accountId, long cycleTotal);

    /**
     * 开户时初始化周期额度并设置下次重置时间。
     *
     * @param tenantId      租户 ID
     * @param accountId     商业账户 ID
     * @param cycleTotal    周期额度总量
     * @param cycleResetAt  下次周期重置时间
     */
    void insertInitialWithReset(Long tenantId, Long accountId, long cycleTotal, java.time.LocalDateTime cycleResetAt);

    /**
     * 查询需要重置周期额度的账户列表。
     *
     * @param now 当前时间
     * @return 待重置额度账户列表
     */
    java.util.List<QuotaAccount> listDueForCycleReset(java.time.LocalDateTime now);

    /**
     * 更新额度账户。
     *
     * @param account 额度账户领域对象
     * @return 受影响行数
     */
    int update(QuotaAccount account);
}
