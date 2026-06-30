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
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
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

    /**
     * 查找人ID。
     *
     * @param id 主键 ID
     * @return 查找结果
     */
    @Override
    public ContentImportJob findById(Long id) {
        ContentImportJobEntity job = importJobMapper.selectById(id);
        if (job == null) {
            throw Errors.of(PlatformErrorCode.CONTENT_IMPORT_JOB_NOT_FOUND);
        }
        return toDomain(job);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @param tenantId 租户 ID
     * @param createBy 创建人 ID
     * @return 创建结果
     */
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

    /**
     * 执行pollPending。
     * @return 执行结果
     */
    @Override
    public ContentImportJob pollPending() {
        ContentImportJobEntity job = importJobMapper.selectOne(new LambdaQueryWrapper<ContentImportJobEntity>()
                .eq(ContentImportJobEntity::getStatus, ContentImportJobStatus.PENDING.code())
                .orderByAsc(ContentImportJobEntity::getId)
                .last("LIMIT 1"));
        return job == null ? null : toDomain(job);
    }

    /**
     * 执行markRunning。
     *
     * @param jobId jobID
     * @return 执行结果
     */
    @Override
    public boolean markRunning(Long jobId) {
        int updated = importJobMapper.update(null, new LambdaUpdateWrapper<ContentImportJobEntity>()
                .eq(ContentImportJobEntity::getId, jobId)
                .eq(ContentImportJobEntity::getStatus, ContentImportJobStatus.PENDING.code())
                .set(ContentImportJobEntity::getStatus, ContentImportJobStatus.RUNNING.code())
                .set(ContentImportJobEntity::getUpdateTime, LocalDateTime.now()));
        return updated > 0;
    }

    /**
     * 执行mark成功。
     *
     * @param jobId jobID
     * @param totalCount 总数数量
     * @param successCount 成功数量
     * @param failCount fail数量
     * @param resultJson 结果JSON
     */
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

    /**
     * 执行markFailed。
     *
     * @param jobId jobID
     * @param message 消息
     */
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

    /**
     * 执行数量人租户And时间Range。
     *
     * @param tenantId 租户 ID
     * @param start start
     * @param end end
     * @return 执行结果
     */
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

    /**
     * 执行数量人状态。
     *
     * @param tenantId 租户 ID
     * @param status 状态
     * @return 执行结果
     */
    @Override
    public long countByStatus(Long tenantId, String status) {
        LambdaQueryWrapper<ContentImportJobEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(ContentImportJobEntity::getTenantId, tenantId);
        }
        wrapper.eq(ContentImportJobEntity::getStatus, status);
        return importJobMapper.selectCount(wrapper);
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
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

    /**
     * 执行escapeJSON。
     *
     * @param value 值
     * @return 执行结果
     */
    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
