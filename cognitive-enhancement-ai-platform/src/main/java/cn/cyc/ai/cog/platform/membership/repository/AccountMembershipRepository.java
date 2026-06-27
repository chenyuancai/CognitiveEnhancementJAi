package cn.cyc.ai.cog.platform.membership.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.dto.MemberLevelRequest;
import cn.cyc.ai.cog.platform.membership.dto.MemberPageQuery;

import java.util.List;

public interface AccountMembershipRepository {

    PageResult<AccountMembership> page(MemberPageQuery query);

    AccountMembership findById(Long id);

    AccountMembership findByAccountId(Long accountId);

    /**
     * 开户时授予初始会员等级。
     *
     * @param tenantId  租户 ID
     * @param accountId 账户 ID
     * @param levelId   等级 ID
     * @param levelCode 等级编码
     * @param source    来源标识
     */
    void grantInitial(Long tenantId, Long accountId, Long levelId, String levelCode, String source);

    AccountMembership upsertGrant(Long accountId, Long levelId, String levelCode,
                                  java.time.LocalDateTime expireAt, String source);

    AccountMembership updateLevel(Long id, MemberLevelRequest request);

    long countByTenant(Long tenantId);

    List<AccountMembership> listByTenant(Long tenantId);

    /**
     * 统计付费会员数（等级编码非 FREE）。
     *
     * @param tenantId 租户 ID，可为 null
     * @return 付费会员数
     */
    long countPaidMembers(Long tenantId);

    /**
     * 统计即将在 N 日内到期的会员数。
     *
     * @param tenantId 租户 ID，可为 null
     * @param days     向前展望天数
     * @return 即将到期会员数
     */
    long countExpiringWithin(Long tenantId, int days);
}
