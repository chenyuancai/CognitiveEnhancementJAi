package cn.cyc.ai.cog.platform.quota.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.platform.quota.domain.QuotaAccount;
import cn.cyc.ai.cog.platform.quota.entity.QuotaAccountEntity;
import cn.cyc.ai.cog.platform.quota.mapper.QuotaAccountMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 额度账户仓储 MyBatis 实现。
 */
@Repository
public class DbQuotaAccountRepository implements QuotaAccountRepository {

    /** 额度账户 Mapper */
    private final QuotaAccountMapper quotaAccountMapper;

    /**
     * @param quotaAccountMapper 额度账户 Mapper
     */
    public DbQuotaAccountRepository(QuotaAccountMapper quotaAccountMapper) {
        this.quotaAccountMapper = quotaAccountMapper;
    }

    @Override
    public QuotaAccount findByAccountId(Long accountId) {
        QuotaAccountEntity entity = quotaAccountMapper.selectOne(new LambdaQueryWrapper<QuotaAccountEntity>()
                .eq(QuotaAccountEntity::getAccountId, accountId)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    @Override
    public QuotaAccount requireByAccountId(Long accountId) {
        QuotaAccount quota = findByAccountId(accountId);
        if (quota == null) {
            throw Errors.of(PlatformErrorCode.QUOTA_ACCOUNT_NOT_FOUND);
        }
        return quota;
    }

    @Override
    public void insertInitial(Long tenantId, Long accountId, long cycleTotal) {
        insertInitialWithReset(tenantId, accountId, cycleTotal, null);
    }

    @Override
    public void insertInitialWithReset(Long tenantId, Long accountId, long cycleTotal, LocalDateTime cycleResetAt) {
        QuotaAccountEntity quota = new QuotaAccountEntity();
        quota.setTenantId(tenantId);
        quota.setAccountId(accountId);
        quota.setCycleRemaining(cycleTotal);
        quota.setCycleTotal(cycleTotal);
        quota.setCycleResetAt(cycleResetAt);
        quota.setGiftRemaining(0L);
        quota.setGiftTotal(0L);
        quota.setTopupRemaining(0L);
        quota.setTopupTotal(0L);
        quotaAccountMapper.insert(quota);
    }

    @Override
    public java.util.List<QuotaAccount> listDueForCycleReset(java.time.LocalDateTime now) {
        return quotaAccountMapper.selectList(new LambdaQueryWrapper<QuotaAccountEntity>()
                        .and(w -> w.isNull(QuotaAccountEntity::getCycleResetAt)
                                .or().le(QuotaAccountEntity::getCycleResetAt, now)))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 更新额度账户，保留数据库乐观锁 version。
     *
     * @param account 待更新的领域对象
     * @return 影响行数
     */
    @Override
    public int update(QuotaAccount account) {
        QuotaAccountEntity entity = toEntity(account);
        QuotaAccountEntity existing = quotaAccountMapper.selectById(account.id());
        if (existing != null) {
            entity.setVersion(existing.getVersion());
        }
        return quotaAccountMapper.updateById(entity);
    }

    private QuotaAccount toDomain(QuotaAccountEntity entity) {
        return new QuotaAccount(
                entity.getId(),
                entity.getTenantId(),
                entity.getAccountId(),
                entity.getCycleRemaining(),
                entity.getCycleTotal(),
                entity.getCycleResetAt(),
                entity.getGiftRemaining(),
                entity.getGiftTotal(),
                entity.getTopupRemaining(),
                entity.getTopupTotal()
        );
    }

    private QuotaAccountEntity toEntity(QuotaAccount account) {
        QuotaAccountEntity entity = new QuotaAccountEntity();
        entity.setId(account.id());
        entity.setTenantId(account.tenantId());
        entity.setAccountId(account.accountId());
        entity.setCycleRemaining(account.cycleRemaining());
        entity.setCycleTotal(account.cycleTotal());
        entity.setCycleResetAt(account.cycleResetAt());
        entity.setGiftRemaining(account.giftRemaining());
        entity.setGiftTotal(account.giftTotal());
        entity.setTopupRemaining(account.topupRemaining());
        entity.setTopupTotal(account.topupTotal());
        return entity;
    }
}
