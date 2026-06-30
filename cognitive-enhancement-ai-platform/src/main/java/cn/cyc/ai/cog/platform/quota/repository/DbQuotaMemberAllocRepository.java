package cn.cyc.ai.cog.platform.quota.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.platform.quota.domain.QuotaMemberAlloc;
import cn.cyc.ai.cog.platform.quota.dto.QuotaMemberAllocSaveRequest;
import cn.cyc.ai.cog.platform.quota.entity.QuotaMemberAllocEntity;
import cn.cyc.ai.cog.platform.quota.mapper.QuotaMemberAllocMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 成员额度分配仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbQuotaMemberAllocRepository implements QuotaMemberAllocRepository {

    /** 成员额度分配 Mapper */
    private final QuotaMemberAllocMapper quotaMemberAllocMapper;

    /**
     * @param quotaMemberAllocMapper 成员额度分配 Mapper
     */
    public DbQuotaMemberAllocRepository(QuotaMemberAllocMapper quotaMemberAllocMapper) {
        this.quotaMemberAllocMapper = quotaMemberAllocMapper;
    }

    /**
     * 查询人账户列表。
     *
     * @param accountId 账户ID
     * @return 结果列表
     */
    @Override
    public List<QuotaMemberAlloc> listByAccount(Long accountId) {
        return quotaMemberAllocMapper.selectList(new LambdaQueryWrapper<QuotaMemberAllocEntity>()
                        .eq(QuotaMemberAllocEntity::getAccountId, accountId)
                        .orderByDesc(QuotaMemberAllocEntity::getId))
                .stream().map(this::toDomain).toList();
    }

    /**
     * 查找人账户And用户。
     *
     * @param accountId 账户ID
     * @param userId 用户 ID
     * @return 查找结果
     */
    @Override
    public QuotaMemberAlloc findByAccountAndUser(Long accountId, Long userId) {
        QuotaMemberAllocEntity entity = quotaMemberAllocMapper.selectOne(new LambdaQueryWrapper<QuotaMemberAllocEntity>()
                .eq(QuotaMemberAllocEntity::getAccountId, accountId)
                .eq(QuotaMemberAllocEntity::getUserId, userId)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    /**
     * 执行allocate。
     *
     * @param accountId 账户ID
     * @param request 请求
     * @return 执行结果
     */
    @Override
    @Transactional
    public QuotaMemberAlloc allocate(Long accountId, QuotaMemberAllocSaveRequest request) {
        QuotaMemberAllocEntity existing = quotaMemberAllocMapper.selectOne(new LambdaQueryWrapper<QuotaMemberAllocEntity>()
                .eq(QuotaMemberAllocEntity::getAccountId, accountId)
                .eq(QuotaMemberAllocEntity::getUserId, request.getUserId())
                .last("LIMIT 1"));
        if (existing == null) {
            QuotaMemberAllocEntity entity = new QuotaMemberAllocEntity();
            entity.setAccountId(accountId);
            entity.setUserId(request.getUserId());
            entity.setAllocatedAmount(request.getAllocatedAmount());
            entity.setUsedAmount(0L);
            quotaMemberAllocMapper.insert(entity);
            return toDomain(entity);
        }
        if (safe(existing.getUsedAmount()) > request.getAllocatedAmount()) {
            throw Errors.of(PlatformErrorCode.QUOTA_ALLOC_BELOW_USED);
        }
        existing.setAllocatedAmount(request.getAllocatedAmount());
        quotaMemberAllocMapper.updateById(existing);
        return toDomain(existing);
    }

    /**
     * 删除Item。
     *
     * @param accountId 账户ID
     * @param userId 用户 ID
     */
    @Override
    @Transactional
    public void remove(Long accountId, Long userId) {
        QuotaMemberAllocEntity existing = quotaMemberAllocMapper.selectOne(new LambdaQueryWrapper<QuotaMemberAllocEntity>()
                .eq(QuotaMemberAllocEntity::getAccountId, accountId)
                .eq(QuotaMemberAllocEntity::getUserId, userId)
                .last("LIMIT 1"));
        if (existing != null) {
            quotaMemberAllocMapper.deleteById(existing.getId());
        }
    }

    /**
     * 更新UsedAmount。
     *
     * @param alloc alloc
     * @return 更新结果
     */
    @Override
    public void updateUsedAmount(QuotaMemberAlloc alloc) {
        QuotaMemberAllocEntity entity = quotaMemberAllocMapper.selectById(alloc.id());
        if (entity != null) {
            entity.setUsedAmount(alloc.usedAmount());
            quotaMemberAllocMapper.updateById(entity);
        }
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private QuotaMemberAlloc toDomain(QuotaMemberAllocEntity entity) {
        return new QuotaMemberAlloc(
                entity.getId(),
                entity.getAccountId(),
                entity.getUserId(),
                entity.getAllocatedAmount(),
                entity.getUsedAmount()
        );
    }

    /**
     * 执行safe。
     *
     * @param value 值
     * @return 执行结果
     */
    private long safe(Long value) {
        return value == null ? 0L : value;
    }
}
