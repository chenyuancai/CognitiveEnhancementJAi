package cn.cyc.ai.cog.platform.system.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.AuditLog;
import cn.cyc.ai.cog.platform.system.dto.AuditLogPageQuery;
import cn.cyc.ai.cog.platform.system.entity.AuditLogEntity;
import cn.cyc.ai.cog.platform.system.mapper.SysAuditLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 审计日志仓储 MyBatis 实现。
 */
@Repository
public class DbAuditLogRepository implements AuditLogRepository {

    /** 审计日志 Mapper */
    private final SysAuditLogMapper auditLogMapper;

    /**
     * @param auditLogMapper 审计日志 Mapper
     */
    public DbAuditLogRepository(SysAuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    public PageResult<AuditLog> page(AuditLogPageQuery query) {
        LambdaQueryWrapper<AuditLogEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getResourceType())) {
            wrapper.eq(AuditLogEntity::getResourceType, query.getResourceType());
        }
        if (query.getOperatorId() != null) {
            wrapper.eq(AuditLogEntity::getOperatorId, query.getOperatorId());
        }
        wrapper.orderByDesc(AuditLogEntity::getId);
        Page<AuditLogEntity> page = auditLogMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public void append(AuditLog log) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setTenantId(log.tenantId());
        entity.setOperatorId(log.operatorId());
        entity.setOperatorName(log.operatorName());
        entity.setAction(log.action());
        entity.setMessage(log.message());
        entity.setResourceType(log.resourceType());
        entity.setResourceId(log.resourceId());
        entity.setBeforeJson(log.beforeJson());
        entity.setAfterJson(log.afterJson());
        entity.setIpAddress(log.ipAddress());
        entity.setCreateTime(log.createTime());
        auditLogMapper.insert(entity);
    }

    private AuditLog toDomain(AuditLogEntity entity) {
        return new AuditLog(
                entity.getId(),
                entity.getTenantId(),
                entity.getOperatorId(),
                entity.getOperatorName(),
                entity.getAction(),
                entity.getMessage(),
                entity.getResourceType(),
                entity.getResourceId(),
                entity.getBeforeJson(),
                entity.getAfterJson(),
                entity.getIpAddress(),
                entity.getCreateTime()
        );
    }
}
