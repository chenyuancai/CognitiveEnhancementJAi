package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentImportJob;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentImportJobCreateRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentImportJobPageQuery;
import cn.cyc.ai.cog.platform.knowledge.entity.ContentImportJobEntity;
import cn.cyc.ai.cog.api.enums.ContentImportJobStatus;
import cn.cyc.ai.cog.platform.knowledge.mapper.ContentImportJobMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 内容导入任务仓储 MyBatis 实现。
 */
@Repository
public class DbContentImportJobRepository implements ContentImportJobRepository {

    /** 内容导入任务 Mapper */
    private final ContentImportJobMapper importJobMapper;

    /**
     * @param importJobMapper 内容导入任务 Mapper
     */
    public DbContentImportJobRepository(ContentImportJobMapper importJobMapper) {
        this.importJobMapper = importJobMapper;
    }

    @Override
    public PageResult<ContentImportJob> page(ContentImportJobPageQuery query) {
        LambdaQueryWrapper<ContentImportJobEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(ContentImportJobEntity::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(ContentImportJobEntity::getId);
        Page<ContentImportJobEntity> page = importJobMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public ContentImportJob findById(Long id) {
        ContentImportJobEntity job = importJobMapper.selectById(id);
        if (job == null) {
            throw Errors.of(PlatformErrorCode.CONTENT_IMPORT_JOB_NOT_FOUND);
        }
        return toDomain(job);
    }

    @Override
    public ContentImportJob create(ContentImportJobCreateRequest request, Long tenantId, Long createBy) {
        ContentImportJobEntity job = new ContentImportJobEntity();
        job.setTenantId(tenantId);
        job.setFileName(request.getFileName().trim());
        job.setFileUrl(request.getFileUrl());
        job.setSourceContent(request.getFileContent());
        job.setStatus(ContentImportJobStatus.PENDING.code());
        job.setTotalCount(0);
        job.setSuccessCount(0);
        job.setFailCount(0);
        job.setCreateBy(createBy);
        job.setCreateTime(LocalDateTime.now());
        job.setUpdateTime(LocalDateTime.now());
        importJobMapper.insert(job);
        return toDomain(job);
    }

    @Override
    public ContentImportJob pollPending() {
        ContentImportJobEntity job = importJobMapper.selectOne(new LambdaQueryWrapper<ContentImportJobEntity>()
                .eq(ContentImportJobEntity::getStatus, ContentImportJobStatus.PENDING.code())
                .orderByAsc(ContentImportJobEntity::getId)
                .last("LIMIT 1"));
        return job == null ? null : toDomain(job);
    }

    @Override
    public boolean markRunning(Long jobId) {
        int updated = importJobMapper.update(null, new LambdaUpdateWrapper<ContentImportJobEntity>()
                .eq(ContentImportJobEntity::getId, jobId)
                .eq(ContentImportJobEntity::getStatus, ContentImportJobStatus.PENDING.code())
                .set(ContentImportJobEntity::getStatus, ContentImportJobStatus.RUNNING.code())
                .set(ContentImportJobEntity::getUpdateTime, LocalDateTime.now()));
        return updated > 0;
    }

    @Override
    public void markSuccess(Long jobId, int totalCount, int successCount, int failCount, String resultJson) {
        importJobMapper.update(null, new LambdaUpdateWrapper<ContentImportJobEntity>()
                .eq(ContentImportJobEntity::getId, jobId)
                .eq(ContentImportJobEntity::getStatus, ContentImportJobStatus.RUNNING.code())
                .set(ContentImportJobEntity::getStatus, ContentImportJobStatus.SUCCESS.code())
                .set(ContentImportJobEntity::getTotalCount, totalCount)
                .set(ContentImportJobEntity::getSuccessCount, successCount)
                .set(ContentImportJobEntity::getFailCount, failCount)
                .set(ContentImportJobEntity::getResultJson, resultJson)
                .set(ContentImportJobEntity::getUpdateTime, LocalDateTime.now()));
    }

    @Override
    public void markFailed(Long jobId, String message) {
        importJobMapper.update(null, new LambdaUpdateWrapper<ContentImportJobEntity>()
                .eq(ContentImportJobEntity::getId, jobId)
                .set(ContentImportJobEntity::getStatus, ContentImportJobStatus.FAILED.code())
                .set(ContentImportJobEntity::getFailCount, 1)
                .set(ContentImportJobEntity::getResultJson, "{\"error\":\""
                        + escapeJson(message == null ? "unknown" : message) + "\"}")
                .set(ContentImportJobEntity::getUpdateTime, LocalDateTime.now()));
    }

    @Override
    public long countByTenantAndTimeRange(Long tenantId, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<ContentImportJobEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(ContentImportJobEntity::getTenantId, tenantId);
        }
        wrapper.ge(ContentImportJobEntity::getCreateTime, start);
        wrapper.le(ContentImportJobEntity::getCreateTime, end);
        return importJobMapper.selectCount(wrapper);
    }

    @Override
    public long countByStatus(Long tenantId, String status) {
        LambdaQueryWrapper<ContentImportJobEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(ContentImportJobEntity::getTenantId, tenantId);
        }
        wrapper.eq(ContentImportJobEntity::getStatus, status);
        return importJobMapper.selectCount(wrapper);
    }

    private ContentImportJob toDomain(ContentImportJobEntity entity) {
        return new ContentImportJob(
                entity.getId(),
                entity.getTenantId(),
                entity.getFileName(),
                entity.getFileUrl(),
                entity.getSourceContent(),
                entity.getStatus(),
                entity.getTotalCount(),
                entity.getSuccessCount(),
                entity.getFailCount(),
                entity.getResultJson(),
                entity.getCreateBy(),
                entity.getCreateTime(),
                entity.getUpdateTime()
        );
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
