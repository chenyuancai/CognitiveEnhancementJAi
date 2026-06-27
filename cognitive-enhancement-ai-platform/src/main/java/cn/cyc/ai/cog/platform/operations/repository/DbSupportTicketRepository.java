package cn.cyc.ai.cog.platform.operations.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.SupportTicket;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketSaveRequest;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketStatusUpdateRequest;
import cn.cyc.ai.cog.platform.operations.entity.SupportTicketEntity;
import cn.cyc.ai.cog.api.enums.SupportTicketStatus;
import cn.cyc.ai.cog.platform.operations.mapper.SupportTicketMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * 客服工单仓储 MyBatis 实现。
 */
@Repository
public class DbSupportTicketRepository implements SupportTicketRepository {

    private static final Set<String> ALLOWED_STATUS = Set.of(
            SupportTicketStatus.OPEN.code(),
            SupportTicketStatus.IN_PROGRESS.code(),
            SupportTicketStatus.RESOLVED.code(),
            SupportTicketStatus.CLOSED.code()
    );

    private final SupportTicketMapper supportTicketMapper;

    public DbSupportTicketRepository(SupportTicketMapper supportTicketMapper) {
        this.supportTicketMapper = supportTicketMapper;
    }

    @Override
    public PageResult<SupportTicket> page(SupportTicketPageQuery query) {
        LambdaQueryWrapper<SupportTicketEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(SupportTicketEntity::getTitle, query.getKeyword())
                    .or()
                    .like(SupportTicketEntity::getTicketNo, query.getKeyword()));
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(SupportTicketEntity::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(SupportTicketEntity::getCategory, query.getCategory());
        }
        if (StringUtils.hasText(query.getPriority())) {
            wrapper.eq(SupportTicketEntity::getPriority, query.getPriority());
        }
        if (query.getAssigneeUserId() != null) {
            wrapper.eq(SupportTicketEntity::getAssigneeUserId, query.getAssigneeUserId());
        }
        if (query.getSubmitterUserId() != null) {
            wrapper.eq(SupportTicketEntity::getSubmitterUserId, query.getSubmitterUserId());
        }
        wrapper.orderByDesc(SupportTicketEntity::getId);
        Page<SupportTicketEntity> page = supportTicketMapper.selectPage(
                Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public SupportTicket findById(Long id) {
        return toDomain(require(id));
    }

    @Override
    public long countPending(Long tenantId) {
        LambdaQueryWrapper<SupportTicketEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(SupportTicketEntity::getTenantId, tenantId);
        }
        wrapper.in(SupportTicketEntity::getStatus,
                SupportTicketStatus.OPEN.code(), SupportTicketStatus.IN_PROGRESS.code());
        return supportTicketMapper.selectCount(wrapper);
    }

    @Override
    public SupportTicket create(SupportTicketSaveRequest request) {
        SupportTicketEntity entity = map(request, new SupportTicketEntity());
        entity.setTicketNo(generateTicketNo());
        entity.setStatus(SupportTicketStatus.OPEN.code());
        supportTicketMapper.insert(entity);
        return toDomain(entity);
    }

    @Override
    public SupportTicket update(Long id, SupportTicketSaveRequest request) {
        SupportTicketEntity entity = map(request, require(id));
        supportTicketMapper.updateById(entity);
        return toDomain(entity);
    }

    @Override
    public SupportTicket updateStatus(Long id, SupportTicketStatusUpdateRequest request) {
        String status = request.getStatus().trim().toUpperCase();
        if (!ALLOWED_STATUS.contains(status)) {
            throw Errors.of(PlatformErrorCode.SUPPORT_TICKET_STATUS_INVALID, "工单状态无效：" + status);
        }
        SupportTicketEntity entity = require(id);
        entity.setStatus(status);
        if (request.getAssigneeUserId() != null) {
            entity.setAssigneeUserId(request.getAssigneeUserId());
        }
        if (SupportTicketStatus.RESOLVED.matches(status) || SupportTicketStatus.CLOSED.matches(status)) {
            entity.setResolvedAt(LocalDateTime.now());
        } else {
            entity.setResolvedAt(null);
        }
        supportTicketMapper.updateById(entity);
        return toDomain(entity);
    }

    @Override
    public void delete(Long id) {
        require(id);
        supportTicketMapper.deleteById(id);
    }

    private SupportTicketEntity map(SupportTicketSaveRequest request, SupportTicketEntity entity) {
        entity.setTitle(request.getTitle().trim());
        entity.setBody(request.getBody());
        entity.setCategory(StringUtils.hasText(request.getCategory()) ? request.getCategory().trim() : null);
        entity.setPriority(StringUtils.hasText(request.getPriority()) ? request.getPriority().trim() : "NORMAL");
        entity.setSubmitterUserId(request.getSubmitterUserId());
        entity.setAssigneeUserId(request.getAssigneeUserId());
        return entity;
    }

    private SupportTicketEntity require(Long id) {
        SupportTicketEntity entity = supportTicketMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.SUPPORT_TICKET_NOT_FOUND);
        }
        return entity;
    }

    private String generateTicketNo() {
        return "TK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);
    }

    private SupportTicket toDomain(SupportTicketEntity entity) {
        return new SupportTicket(
                entity.getId(),
                entity.getTicketNo(),
                entity.getTitle(),
                entity.getBody(),
                entity.getCategory(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getSubmitterUserId(),
                entity.getAssigneeUserId(),
                entity.getResolvedAt()
        );
    }
}
