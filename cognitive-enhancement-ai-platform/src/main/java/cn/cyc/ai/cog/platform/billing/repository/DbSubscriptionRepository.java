package cn.cyc.ai.cog.platform.billing.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.domain.Subscription;
import cn.cyc.ai.cog.platform.billing.entity.SubscriptionPhase;
import cn.cyc.ai.cog.platform.billing.entity.SubscriptionEntity;
import cn.cyc.ai.cog.platform.billing.mapper.SubscriptionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订阅记录仓储 MyBatis 实现。
 */
@Repository
public class DbSubscriptionRepository implements SubscriptionRepository {

    /** 订阅记录 Mapper */
    private final SubscriptionMapper subscriptionMapper;

    /**
     * @param subscriptionMapper 订阅记录 Mapper
     */
    public DbSubscriptionRepository(SubscriptionMapper subscriptionMapper) {
        this.subscriptionMapper = subscriptionMapper;
    }

    @Override
    public PageResult<Subscription> page(long current, long size, Long accountId) {
        LambdaQueryWrapper<SubscriptionEntity> wrapper = new LambdaQueryWrapper<>();
        if (accountId != null) {
            wrapper.eq(SubscriptionEntity::getAccountId, accountId);
        }
        wrapper.orderByDesc(SubscriptionEntity::getId);
        Page<SubscriptionEntity> page = subscriptionMapper.selectPage(Page.of(current, size), wrapper);
        return PageResult.of(
                page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
    }

    @Override
    public Subscription findByOrderId(Long orderId) {
        SubscriptionEntity entity = subscriptionMapper.selectOne(new LambdaQueryWrapper<SubscriptionEntity>()
                .eq(SubscriptionEntity::getOrderId, orderId)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    @Override
    public Subscription insertActive(Long tenantId, Long accountId, Long orderId, Long packageId,
                                     String levelCode, String phase, LocalDateTime startAt, LocalDateTime endAt) {
        SubscriptionEntity sub = new SubscriptionEntity();
        sub.setTenantId(tenantId);
        sub.setAccountId(accountId);
        sub.setOrderId(orderId);
        sub.setPackageId(packageId);
        sub.setLevelCode(levelCode);
        sub.setStatus("ACTIVE");
        sub.setPhase(phase);
        sub.setStartAt(startAt);
        sub.setEndAt(endAt);
        sub.setAutoRenew(false);
        subscriptionMapper.insert(sub);
        return toDomain(sub);
    }

    @Override
    public void markRefunded(Long subscriptionId) {
        SubscriptionEntity sub = subscriptionMapper.selectById(subscriptionId);
        if (sub != null) {
            sub.setStatus("REFUNDED");
            subscriptionMapper.updateById(sub);
        }
    }

    @Override
    public List<Subscription> listTrialExpiredActive(LocalDateTime now) {
        return subscriptionMapper.selectList(new LambdaQueryWrapper<SubscriptionEntity>()
                        .eq(SubscriptionEntity::getStatus, "ACTIVE")
                        .eq(SubscriptionEntity::getPhase, SubscriptionPhase.TRIAL)
                        .lt(SubscriptionEntity::getEndAt, now))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Subscription> listFormalExpiredActive(LocalDateTime now) {
        return subscriptionMapper.selectList(new LambdaQueryWrapper<SubscriptionEntity>()
                        .eq(SubscriptionEntity::getStatus, "ACTIVE")
                        .and(w -> w.eq(SubscriptionEntity::getPhase, SubscriptionPhase.FORMAL)
                                .or().isNull(SubscriptionEntity::getPhase))
                        .ne(SubscriptionEntity::getLevelCode, "FREE")
                        .lt(SubscriptionEntity::getEndAt, now))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void advanceToFormal(Long subscriptionId, LocalDateTime endAt) {
        SubscriptionEntity sub = subscriptionMapper.selectById(subscriptionId);
        if (sub == null) {
            return;
        }
        sub.setPhase(SubscriptionPhase.FORMAL);
        sub.setEndAt(endAt);
        subscriptionMapper.updateById(sub);
    }

    @Override
    @Deprecated
    public List<Subscription> listExpiredActive(LocalDateTime now) {
        return listFormalExpiredActive(now);
    }

    @Override
    public void markExpired(Long subscriptionId) {
        SubscriptionEntity sub = subscriptionMapper.selectById(subscriptionId);
        if (sub != null) {
            sub.setStatus("EXPIRED");
            subscriptionMapper.updateById(sub);
        }
    }

    @Override
    public Subscription findActiveValidByAccountId(Long accountId, LocalDateTime now) {
        SubscriptionEntity entity = subscriptionMapper.selectOne(new LambdaQueryWrapper<SubscriptionEntity>()
                .eq(SubscriptionEntity::getAccountId, accountId)
                .eq(SubscriptionEntity::getStatus, "ACTIVE")
                .gt(SubscriptionEntity::getEndAt, now)
                .orderByDesc(SubscriptionEntity::getId)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    private Subscription toDomain(SubscriptionEntity entity) {
        return new Subscription(
                entity.getId(),
                entity.getTenantId(),
                entity.getAccountId(),
                entity.getOrderId(),
                entity.getPackageId(),
                entity.getLevelCode(),
                entity.getStatus(),
                entity.getPhase(),
                entity.getStartAt(),
                entity.getEndAt(),
                entity.getAutoRenew(),
                entity.getPackageSnapshotJson()
        );
    }
}
