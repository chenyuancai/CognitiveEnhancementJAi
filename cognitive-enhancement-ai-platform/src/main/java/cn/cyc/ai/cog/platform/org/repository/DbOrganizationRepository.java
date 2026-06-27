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

    @Override
    public Organization requireById(Long orgId) {
        OrganizationEntity entity = organizationMapper.selectById(orgId);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.ORG_NOT_FOUND);
        }
        return toDomain(entity);
    }

    @Override
    public Organization findByAccountId(Long accountId) {
        OrganizationEntity entity = organizationMapper.selectOne(new LambdaQueryWrapper<OrganizationEntity>()
                .eq(OrganizationEntity::getAccountId, accountId)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    @Override
    public Organization insert(Organization organization) {
        OrganizationEntity entity = toEntity(organization);
        organizationMapper.insert(entity);
        return toDomain(entity);
    }

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
