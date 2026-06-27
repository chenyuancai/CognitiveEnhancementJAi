package cn.cyc.ai.cog.platform.account.repository;

import cn.cyc.ai.cog.platform.account.domain.Account;

/**
 * 商业账户仓储接口。
 */
public interface AccountRepository {

    /**
     * 按 ID 查询账户，不存在时返回 null。
     *
     * @param id 账户 ID
     * @return 账户领域对象或 null
     */
    Account findById(Long id);

    /**
     * 按负责人用户 ID 查询账户，不存在时返回 null。
     *
     * @param ownerUserId 负责人用户 ID
     * @return 账户领域对象或 null
     */
    Account findByOwnerUserId(Long ownerUserId);

    /**
     * 按负责人用户 ID 查询个人账户，不存在时返回 null。
     *
     * @param ownerUserId 负责人用户 ID
     * @return 个人账户或 null
     */
    Account findIndividualByOwnerUserId(Long ownerUserId);

    /**
     * 按 ID 查询账户，不存在时抛出业务异常。
     *
     * @param id 账户 ID
     * @return 账户领域对象
     */
    Account requireById(Long id);

    /**
     * 新增账户并返回持久化后的领域对象。
     *
     * @param account 待插入账户（id 可为 null）
     * @return 持久化后的账户
     */
    Account insert(Account account);
}
