package cn.cyc.ai.cog.platform.account.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.platform.account.domain.Account;
import cn.cyc.ai.cog.platform.account.entity.AccountEntity;
import cn.cyc.ai.cog.platform.account.mapper.AccountMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

/**
 * 商业账户 MyBatis 仓储实现。
 */
/**
 * 商业账户仓储 MyBatis 实现。
 */
@Repository
public class DbAccountRepository implements AccountRepository {

    /** 商业账户 Mapper */
    private final AccountMapper accountMapper;

    /**
     * @param accountMapper 商业账户 Mapper
     */
    public DbAccountRepository(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public Account findById(Long id) {
        AccountEntity entity = accountMapper.selectById(id);
        return entity == null ? null : toDomain(entity);
    }

    @Override
    public Account findByOwnerUserId(Long ownerUserId) {
        AccountEntity entity = accountMapper.selectOne(new LambdaQueryWrapper<AccountEntity>()
                .eq(AccountEntity::getOwnerUserId, ownerUserId)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    @Override
    public Account findIndividualByOwnerUserId(Long ownerUserId) {
        AccountEntity entity = accountMapper.selectOne(new LambdaQueryWrapper<AccountEntity>()
                .eq(AccountEntity::getOwnerUserId, ownerUserId)
                .eq(AccountEntity::getAccountType, "INDIVIDUAL")
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    @Override
    public Account requireById(Long id) {
        Account account = findById(id);
        if (account == null) {
            throw Errors.of(PlatformErrorCode.ACCOUNT_NOT_FOUND, "账户不存在：" + id);
        }
        return account;
    }

    @Override
    public Account insert(Account account) {
        AccountEntity entity = toEntity(account);
        accountMapper.insert(entity);
        return toDomain(entity);
    }

    private Account toDomain(AccountEntity entity) {
        return new Account(
                entity.getId(),
                entity.getTenantId(),
                entity.getAccountType(),
                entity.getSegment(),
                entity.getDisplayName(),
                entity.getOwnerUserId(),
                entity.getStatus()
        );
    }

    private AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity();
        entity.setId(account.id());
        entity.setTenantId(account.tenantId());
        entity.setAccountType(account.accountType());
        entity.setSegment(account.segment());
        entity.setDisplayName(account.displayName());
        entity.setOwnerUserId(account.ownerUserId());
        entity.setStatus(account.status());
        return entity;
    }
}
