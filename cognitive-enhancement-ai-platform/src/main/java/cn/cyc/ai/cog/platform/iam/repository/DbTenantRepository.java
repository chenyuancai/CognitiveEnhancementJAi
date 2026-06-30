package cn.cyc.ai.cog.platform.iam.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.api.enums.EnableStatus;
import cn.cyc.ai.cog.api.enums.IamUserStatus;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.iam.domain.Tenant;
import cn.cyc.ai.cog.platform.iam.dto.TenantPageQuery;
import cn.cyc.ai.cog.platform.iam.dto.TenantSaveRequest;
import cn.cyc.ai.cog.platform.iam.entity.SysTenantEntity;
import cn.cyc.ai.cog.platform.iam.mapper.SysTenantMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 租户 MyBatis 仓储实现。
 */
/**
 * 租户仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbTenantRepository implements TenantRepository {

    /** 租户 Mapper */
    private final SysTenantMapper sysTenantMapper;

    /**
     * @param sysTenantMapper 租户 Mapper
     */
    public DbTenantRepository(SysTenantMapper sysTenantMapper) {
        this.sysTenantMapper = sysTenantMapper;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Override
    public PageResult<Tenant> page(TenantPageQuery query) {
        LambdaQueryWrapper<SysTenantEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(SysTenantEntity::getTenantCode, query.getKeyword())
                    .or().like(SysTenantEntity::getTenantName, query.getKeyword()));
        }
        if (StringUtils.hasText(query.getSegment())) {
            wrapper.eq(SysTenantEntity::getSegment, query.getSegment());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(SysTenantEntity::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysTenantEntity::getId);
        Page<SysTenantEntity> page = sysTenantMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(
                page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
    }

    /**
     * 执行require人ID。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Override
    public Tenant requireById(Long id) {
        SysTenantEntity entity = sysTenantMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.TENANT_NOT_FOUND);
        }
        return toDomain(entity);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Override
    public Tenant create(TenantSaveRequest request) {
        assertCodeUnique(request.getTenantCode(), null);
        SysTenantEntity tenant = new SysTenantEntity();
        mapRequest(tenant, request);
        LocalDateTime now = LocalDateTime.now();
        tenant.setCreateTime(now);
        tenant.setUpdateTime(now);
        tenant.setVersion(0);
        sysTenantMapper.insert(tenant);
        return toDomain(tenant);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    @Override
    public Tenant update(Long id, TenantSaveRequest request) {
        SysTenantEntity tenant = sysTenantMapper.selectById(id);
        if (tenant == null) {
            throw Errors.of(PlatformErrorCode.TENANT_NOT_FOUND);
        }
        if (id.equals(CommonConstants.DEFAULT_TENANT_ID)
                && !CommonConstants.DEFAULT_TENANT.equals(request.getTenantCode())) {
            throw Errors.of(PlatformErrorCode.DEFAULT_TENANT_CODE_IMMUTABLE);
        }
        assertCodeUnique(request.getTenantCode(), id);
        mapRequest(tenant, request);
        tenant.setUpdateTime(LocalDateTime.now());
        sysTenantMapper.updateById(tenant);
        return toDomain(sysTenantMapper.selectById(id));
    }

    /**
     * 更新状态。
     *
     * @param id 主键 ID
     * @param status 状态
     * @return 更新结果
     */
    @Override
    public Tenant updateStatus(Long id, String status) {
        if (!EnableStatus.isValid(status)) {
            throw Errors.of(PlatformErrorCode.TENANT_STATUS_INVALID, "非法状态：" + status);
        }
        if (id.equals(CommonConstants.DEFAULT_TENANT_ID)) {
            throw Errors.of(PlatformErrorCode.DEFAULT_TENANT_NOT_DISABLE);
        }
        SysTenantEntity tenant = sysTenantMapper.selectById(id);
        if (tenant == null) {
            throw Errors.of(PlatformErrorCode.TENANT_NOT_FOUND);
        }
        tenant.setStatus(status);
        tenant.setUpdateTime(LocalDateTime.now());
        sysTenantMapper.updateById(tenant);
        return toDomain(tenant);
    }

    /**
     * 执行insertForOrganization。
     *
     * @param tenantCode 租户编码
     * @param tenantName 租户名称
     * @param segment segment
     * @return 执行结果
     */
    @Override
    public Tenant insertForOrganization(String tenantCode, String tenantName, String segment) {
        SysTenantEntity tenant = new SysTenantEntity();
        tenant.setTenantCode(tenantCode);
        tenant.setTenantName(tenantName);
        tenant.setSegment(segment);
        tenant.setStatus(CommonConstants.STATUS_ENABLED);
        LocalDateTime now = LocalDateTime.now();
        tenant.setCreateTime(now);
        tenant.setUpdateTime(now);
        tenant.setVersion(0);
        sysTenantMapper.insert(tenant);
        return toDomain(tenant);
    }

    /**
     * 执行map请求。
     *
     * @param tenant 租户
     * @param request 请求
     */
    private void mapRequest(SysTenantEntity tenant, TenantSaveRequest request) {
        tenant.setTenantCode(request.getTenantCode().trim());
        tenant.setTenantName(request.getTenantName().trim());
        tenant.setSegment(request.getSegment().trim().toUpperCase());
        tenant.setStatus(StringUtils.hasText(request.getStatus())
                ? request.getStatus().trim().toUpperCase()
                : CommonConstants.STATUS_ENABLED);
    }

    /**
     * 执行assert编码Unique。
     *
     * @param tenantCode 租户编码
     * @param excludeId excludeID
     */
    private void assertCodeUnique(String tenantCode, Long excludeId) {
        SysTenantEntity existing = sysTenantMapper.selectOne(new LambdaQueryWrapper<SysTenantEntity>()
                .eq(SysTenantEntity::getTenantCode, tenantCode.trim())
                .last("LIMIT 1"));
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw Errors.of(PlatformErrorCode.TENANT_CODE_EXISTS, "租户编码已存在：" + tenantCode);
        }
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private Tenant toDomain(SysTenantEntity entity) {
        return new Tenant(
                entity.getId(),
                entity.getTenantCode(),
                entity.getTenantName(),
                entity.getSegment(),
                entity.getStatus(),
                entity.getCreateTime(),
                entity.getUpdateTime()
        );
    }
}
