package cn.cyc.ai.cog.platform.billing.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.domain.QuotaPackage;
import cn.cyc.ai.cog.platform.billing.dto.PackagePageQuery;
import cn.cyc.ai.cog.platform.billing.dto.QuotaPackageSaveRequest;
import cn.cyc.ai.cog.platform.billing.entity.QuotaPackageEntity;
import cn.cyc.ai.cog.platform.billing.mapper.QuotaPackageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 额度包 MyBatis 仓储实现。
 */
/**
 * 额度包仓储 MyBatis 实现。
 */
@Repository
public class DbQuotaPackageRepository implements QuotaPackageRepository {

    /** 额度包 Mapper */
    private final QuotaPackageMapper quotaPackageMapper;

    /**
     * @param quotaPackageMapper 额度包 Mapper
     */
    public DbQuotaPackageRepository(QuotaPackageMapper quotaPackageMapper) {
        this.quotaPackageMapper = quotaPackageMapper;
    }

    @Override
    public PageResult<QuotaPackage> page(PackagePageQuery query) {
        Page<QuotaPackageEntity> page = quotaPackageMapper.selectPage(
                Page.of(query.getCurrent(), query.getSize()),
                new LambdaQueryWrapper<QuotaPackageEntity>().orderByDesc(QuotaPackageEntity::getId));
        return PageResult.of(
                page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
    }

    @Override
    public List<QuotaPackage> listOnSale(String segment) {
        LambdaQueryWrapper<QuotaPackageEntity> wrapper = new LambdaQueryWrapper<QuotaPackageEntity>()
                .eq(QuotaPackageEntity::getStatus, "ON_SALE")
                .orderByAsc(QuotaPackageEntity::getId);
        if (StringUtils.hasText(segment)) {
            wrapper.eq(QuotaPackageEntity::getSegment, segment);
        }
        return quotaPackageMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public QuotaPackage requireById(Long id) {
        QuotaPackageEntity entity = quotaPackageMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.QUOTA_PACKAGE_NOT_FOUND);
        }
        return toDomain(entity);
    }

    @Override
    public QuotaPackage save(Long id, QuotaPackageSaveRequest request) {
        QuotaPackageEntity entity = id == null ? new QuotaPackageEntity() : quotaPackageMapper.selectById(id);
        if (id != null && entity == null) {
            throw Errors.of(PlatformErrorCode.QUOTA_PACKAGE_NOT_FOUND);
        }
        mapRequest(entity, request);
        if (id == null) {
            quotaPackageMapper.insert(entity);
        } else {
            quotaPackageMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    private void mapRequest(QuotaPackageEntity entity, QuotaPackageSaveRequest request) {
        entity.setPackageCode(request.getPackageCode());
        entity.setPackageName(request.getPackageName());
        entity.setSegment(request.getSegment());
        entity.setTokenAmount(request.getTokenAmount());
        entity.setPriceFen(request.getPriceFen());
        entity.setValidDays(request.getValidDays());
        entity.setStatus(request.getStatus());
    }

    private QuotaPackage toDomain(QuotaPackageEntity entity) {
        return new QuotaPackage(
                entity.getId(),
                entity.getTenantId(),
                entity.getPackageCode(),
                entity.getPackageName(),
                entity.getSegment(),
                entity.getTokenAmount(),
                entity.getPriceFen(),
                entity.getValidDays(),
                entity.getStatus()
        );
    }
}
