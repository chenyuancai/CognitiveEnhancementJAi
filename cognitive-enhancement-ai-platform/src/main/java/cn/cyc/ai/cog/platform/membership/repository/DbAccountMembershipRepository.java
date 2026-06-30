package cn.cyc.ai.cog.platform.membership.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.dto.MemberLevelRequest;
import cn.cyc.ai.cog.platform.membership.dto.MemberPageQuery;
import cn.cyc.ai.cog.platform.membership.entity.AccountMembershipEntity;
import cn.cyc.ai.cog.platform.membership.mapper.AccountMembershipMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 账户会员关系仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbAccountMembershipRepository implements AccountMembershipRepository {

    /** 账户会员关系 Mapper */
    private final AccountMembershipMapper accountMembershipMapper;

    /**
     * @param accountMembershipMapper 账户会员关系 Mapper
     */
    public DbAccountMembershipRepository(AccountMembershipMapper accountMembershipMapper) {
        this.accountMembershipMapper = accountMembershipMapper;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Override
    public PageResult<AccountMembership> page(MemberPageQuery query) {
        LambdaQueryWrapper<AccountMembershipEntity> wrapper = new LambdaQueryWrapper<>();
        if (query.getAccountId() != null) {
            wrapper.eq(AccountMembershipEntity::getAccountId, query.getAccountId());
        }
        if (StringUtils.hasText(query.getLevelCode())) {
            wrapper.eq(AccountMembershipEntity::getLevelCode, query.getLevelCode());
        }
        wrapper.orderByDesc(AccountMembershipEntity::getId);
        Page<AccountMembershipEntity> page = accountMembershipMapper.selectPage(
                Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 查找人ID。
     *
     * @param id 主键 ID
     * @return 查找结果
     */
    @Override
    public AccountMembership findById(Long id) {
        AccountMembershipEntity entity = accountMembershipMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.MEMBERSHIP_NOT_FOUND, "会员记录不存在：" + id);
        }
        return toDomain(entity);
    }

    /**
     * 查找人账户ID。
     *
     * @param accountId 账户ID
     * @return 查找结果
     */
    @Override
    public AccountMembership findByAccountId(Long accountId) {
        AccountMembershipEntity entity = accountMembershipMapper.selectOne(new LambdaQueryWrapper<AccountMembershipEntity>()
                .eq(AccountMembershipEntity::getAccountId, accountId)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    /**
     * 执行grantInitial。
     *
     * @param tenantId 租户 ID
     * @param accountId 账户ID
     * @param levelId 等级ID
     * @param levelCode 等级编码
     * @param source 来源
     */
    @Override
    public void grantInitial(Long tenantId, Long accountId, Long levelId, String levelCode, String source) {
        AccountMembershipEntity membership = new AccountMembershipEntity();
        membership.setTenantId(tenantId);
        membership.setAccountId(accountId);
        membership.setLevelId(levelId);
        membership.setLevelCode(levelCode);
        membership.setSource(source);
        accountMembershipMapper.insert(membership);
    }

    /**
     * 执行upsertGrant。
     * @return 执行结果
     */
    @Override
    public AccountMembership upsertGrant(Long accountId, Long levelId, String levelCode,
                                         java.time.LocalDateTime expireAt, String source) {
        AccountMembershipEntity membership = accountMembershipMapper.selectOne(new LambdaQueryWrapper<AccountMembershipEntity>()
                .eq(AccountMembershipEntity::getAccountId, accountId)
                .last("LIMIT 1"));
        if (membership == null) {
            membership = new AccountMembershipEntity();
            membership.setAccountId(accountId);
            membership.setLevelId(levelId);
            membership.setLevelCode(levelCode);
            membership.setExpireAt(expireAt);
            membership.setSource(source);
            accountMembershipMapper.insert(membership);
        } else {
            membership.setLevelId(levelId);
            membership.setLevelCode(levelCode);
            membership.setExpireAt(expireAt);
            membership.setSource(source);
            accountMembershipMapper.updateById(membership);
        }
        return toDomain(membership);
    }

    /**
     * 更新等级。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    @Override
    public AccountMembership updateLevel(Long id, MemberLevelRequest request) {
        AccountMembershipEntity membership = accountMembershipMapper.selectById(id);
        if (membership == null) {
            throw Errors.of(PlatformErrorCode.MEMBERSHIP_NOT_FOUND, "会员记录不存在：" + id);
        }
        membership.setLevelCode(request.getLevelCode());
        membership.setLevelId(request.getLevelId());
        membership.setExpireAt(request.getExpireAt());
        membership.setSource("GRANT");
        accountMembershipMapper.updateById(membership);
        return toDomain(membership);
    }

    /**
     * 执行数量人租户。
     *
     * @param tenantId 租户 ID
     * @return 执行结果
     */
    @Override
    public long countByTenant(Long tenantId) {
        LambdaQueryWrapper<AccountMembershipEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(AccountMembershipEntity::getTenantId, tenantId);
        }
        return accountMembershipMapper.selectCount(wrapper);
    }

    /**
     * 查询人租户列表。
     *
     * @param tenantId 租户 ID
     * @return 结果列表
     */
    @Override
    public List<AccountMembership> listByTenant(Long tenantId) {
        LambdaQueryWrapper<AccountMembershipEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(AccountMembershipEntity::getTenantId, tenantId);
        }
        return accountMembershipMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    /**
     * 执行数量PaidMembers。
     *
     * @param tenantId 租户 ID
     * @return 执行结果
     */
    @Override
    public long countPaidMembers(Long tenantId) {
        LambdaQueryWrapper<AccountMembershipEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(AccountMembershipEntity::getTenantId, tenantId);
        }
        wrapper.ne(AccountMembershipEntity::getLevelCode, "FREE");
        return accountMembershipMapper.selectCount(wrapper);
    }

    /**
     * 执行数量ExpiringWithin。
     *
     * @param tenantId 租户 ID
     * @param days days
     * @return 执行结果
     */
    @Override
    public long countExpiringWithin(Long tenantId, int days) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime deadline = now.plusDays(Math.max(days, 1));
        LambdaQueryWrapper<AccountMembershipEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(AccountMembershipEntity::getTenantId, tenantId);
        }
        wrapper.isNotNull(AccountMembershipEntity::getExpireAt);
        wrapper.gt(AccountMembershipEntity::getExpireAt, now);
        wrapper.le(AccountMembershipEntity::getExpireAt, deadline);
        return accountMembershipMapper.selectCount(wrapper);
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private AccountMembership toDomain(AccountMembershipEntity entity) {
        return new AccountMembership(
                entity.getId(),
                entity.getTenantId(),
                entity.getAccountId(),
                entity.getLevelId(),
                entity.getLevelCode(),
                entity.getExpireAt(),
                entity.getSource()
        );
    }
}
