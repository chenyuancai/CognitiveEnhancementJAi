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
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
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

    /**
     * 查询OnSale列表。
     *
     * @param segment segment
     * @return 结果列表
     */
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

    /**
     * 执行require人ID。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Override
    public QuotaPackage requireById(Long id) {
        QuotaPackageEntity entity = quotaPackageMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.QUOTA_PACKAGE_NOT_FOUND);
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

    /**
     * 执行map请求。
     *
     * @param entity 实体
     * @param request 请求
     */
    private void mapRequest(QuotaPackageEntity entity, QuotaPackageSaveRequest request) {
        entity.setPackageCode(request.getPackageCode());
        entity.setPackageName(request.getPackageName());
        entity.setSegment(request.getSegment());
        entity.setTokenAmount(request.getTokenAmount());
        entity.setPriceFen(request.getPriceFen());
        entity.setValidDays(request.getValidDays());
        entity.setStatus(request.getStatus());
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
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
