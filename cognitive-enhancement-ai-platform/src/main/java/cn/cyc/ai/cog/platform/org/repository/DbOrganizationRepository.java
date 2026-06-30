package cn.cyc.ai.cog.platform.org.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.org.domain.Organization;
import cn.cyc.ai.cog.platform.org.dto.OrgPageQuery;
import cn.cyc.ai.cog.platform.org.entity.OrganizationEntity;
import cn.cyc.ai.cog.platform.org.mapper.OrganizationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 组织 MyBatis 仓储实现。
 */
/**
 * 组织仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbOrganizationRepository implements OrganizationRepository {

    /** 组织 Mapper */
    private final OrganizationMapper organizationMapper;

    /**
     * @param organizationMapper 组织 Mapper
     */
    public DbOrganizationRepository(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Override
    public PageResult<Organization> page(OrgPageQuery query) {
        LambdaQueryWrapper<OrganizationEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(OrganizationEntity::getOrgName, query.getKeyword());
        }
        wrapper.orderByDesc(OrganizationEntity::getId);
        Page<OrganizationEntity> page = organizationMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(
                page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
    }

    /**
     * 执行require人ID。
     *
     * @param orgId orgID
     * @return 执行结果
     */
    @Override
    public Organization requireById(Long orgId) {
        OrganizationEntity entity = organizationMapper.selectById(orgId);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.ORG_NOT_FOUND);
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
    public Organization findByAccountId(Long accountId) {
        OrganizationEntity entity = organizationMapper.selectOne(new LambdaQueryWrapper<OrganizationEntity>()
                .eq(OrganizationEntity::getAccountId, accountId)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    /**
     * 执行insert。
     *
     * @param organization organization
     * @return 执行结果
     */
    @Override
    public Organization insert(Organization organization) {
        OrganizationEntity entity = toEntity(organization);
        organizationMapper.insert(entity);
        return toDomain(entity);
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private Organization toDomain(OrganizationEntity entity) {
        return new Organization(
                entity.getId(),
                entity.getTenantId(),
                entity.getAccountId(),
                entity.getOrgType(),
                entity.getOrgName(),
                entity.getUnifiedSocialCode(),
                entity.getSeatLimit(),
                entity.getContactName(),
                entity.getContactPhone(),
                entity.getCreateTime(),
                entity.getUpdateTime()
        );
    }

    /**
     * 转换为实体。
     *
     * @param organization organization
     * @return 转换结果
     */
    private OrganizationEntity toEntity(Organization organization) {
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(organization.id());
        entity.setTenantId(organization.tenantId());
        entity.setAccountId(organization.accountId());
        entity.setOrgType(organization.orgType());
        entity.setOrgName(organization.orgName());
        entity.setUnifiedSocialCode(organization.unifiedSocialCode());
        entity.setSeatLimit(organization.seatLimit());
        entity.setContactName(organization.contactName());
        entity.setContactPhone(organization.contactPhone());
        return entity;
    }
}
