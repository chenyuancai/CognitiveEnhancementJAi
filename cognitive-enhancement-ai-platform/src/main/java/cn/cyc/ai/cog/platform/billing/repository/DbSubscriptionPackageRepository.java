package cn.cyc.ai.cog.platform.billing.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import cn.cyc.ai.cog.platform.billing.dto.PackagePageQuery;
import cn.cyc.ai.cog.platform.billing.dto.SubscriptionPackageSaveRequest;
import cn.cyc.ai.cog.platform.billing.entity.SubscriptionPackageEntity;
import cn.cyc.ai.cog.platform.billing.mapper.SubscriptionPackageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 订阅套餐 MyBatis 仓储实现。
 */
/**
 * 订阅套餐仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbSubscriptionPackageRepository implements SubscriptionPackageRepository {

    /** 订阅套餐 Mapper */
    private final SubscriptionPackageMapper subscriptionPackageMapper;

    /**
     * @param subscriptionPackageMapper 订阅套餐 Mapper
     */
    public DbSubscriptionPackageRepository(SubscriptionPackageMapper subscriptionPackageMapper) {
        this.subscriptionPackageMapper = subscriptionPackageMapper;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Override
    public PageResult<SubscriptionPackage> page(PackagePageQuery query) {
        Page<SubscriptionPackageEntity> page = subscriptionPackageMapper.selectPage(
                Page.of(query.getCurrent(), query.getSize()),
                new LambdaQueryWrapper<SubscriptionPackageEntity>().orderByDesc(SubscriptionPackageEntity::getId));
        return PageResult.of(
                page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
    }

    /**
     * 查询OnSale列表。
     *
     * @param segment segment
     * @return 结果列表
     */
    @Override
    public List<SubscriptionPackage> listOnSale(String segment) {
        LambdaQueryWrapper<SubscriptionPackageEntity> wrapper = new LambdaQueryWrapper<SubscriptionPackageEntity>()
                .eq(SubscriptionPackageEntity::getStatus, "ON_SALE")
                .orderByAsc(SubscriptionPackageEntity::getId);
        if (StringUtils.hasText(segment)) {
            wrapper.eq(SubscriptionPackageEntity::getSegment, segment);
        }
        return subscriptionPackageMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    /**
     * 查找人Package编码。
     *
     * @param packageCode package编码
     * @return 查找结果
     */
    @Override
    public SubscriptionPackage findByPackageCode(String packageCode) {
        if (!StringUtils.hasText(packageCode)) {
            return null;
        }
        SubscriptionPackageEntity entity = subscriptionPackageMapper.selectOne(
                new LambdaQueryWrapper<SubscriptionPackageEntity>()
                        .eq(SubscriptionPackageEntity::getPackageCode, packageCode.trim())
                        .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    /**
     * 执行require人ID。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Override
    public SubscriptionPackage requireById(Long id) {
        SubscriptionPackageEntity entity = subscriptionPackageMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.SUBSCRIPTION_PACKAGE_NOT_FOUND);
        }
        return toDomain(entity);
    }

    /**
     * 执行save。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 执行结果
     */
    @Override
    public SubscriptionPackage save(Long id, SubscriptionPackageSaveRequest request) {
        SubscriptionPackageEntity entity = id == null ? new SubscriptionPackageEntity() : subscriptionPackageMapper.selectById(id);
        if (id != null && entity == null) {
            throw Errors.of(PlatformErrorCode.SUBSCRIPTION_PACKAGE_NOT_FOUND);
        }
        mapRequest(entity, request);
        if (id == null) {
            subscriptionPackageMapper.insert(entity);
        } else {
            subscriptionPackageMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    /**
     * 执行map请求。
     *
     * @param entity 实体
     * @param request 请求
     */
    private void mapRequest(SubscriptionPackageEntity entity, SubscriptionPackageSaveRequest request) {
        entity.setPackageCode(request.getPackageCode());
        entity.setPackageName(request.getPackageName());
        entity.setSegment(request.getSegment());
        entity.setLevelId(request.getLevelId());
        entity.setBillingPeriod(request.getBillingPeriod());
        entity.setPeriodCount(request.getPeriodCount());
        entity.setTrialDays(request.getTrialDays() == null ? 0 : request.getTrialDays());
        entity.setPriceFen(request.getPriceFen());
        entity.setOriginalPriceFen(request.getOriginalPriceFen());
        entity.setCycleTokenQuota(request.getCycleTokenQuota());
        entity.setSeatCount(request.getSeatCount());
        entity.setSaleMode(request.getSaleMode());
        entity.setRequireContract(Boolean.TRUE.equals(request.getRequireContract()));
        entity.setStatus(request.getStatus());
        entity.setSnapshotJson(request.getSnapshotJson());
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private SubscriptionPackage toDomain(SubscriptionPackageEntity entity) {
        return new SubscriptionPackage(
                entity.getId(),
                entity.getTenantId(),
                entity.getPackageCode(),
                entity.getPackageName(),
                entity.getSegment(),
                entity.getLevelId(),
                entity.getBillingPeriod(),
                entity.getPeriodCount(),
                entity.getPriceFen(),
                entity.getOriginalPriceFen(),
                entity.getCycleTokenQuota(),
                entity.getSeatCount(),
                entity.getSaleMode(),
                entity.getRequireContract(),
                entity.getStatus(),
                entity.getSnapshotJson(),
                entity.getTrialDays()
        );
    }
}
