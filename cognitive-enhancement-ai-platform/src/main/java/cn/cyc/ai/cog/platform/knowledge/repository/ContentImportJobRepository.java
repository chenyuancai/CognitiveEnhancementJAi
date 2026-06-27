package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentImportJob;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentImportJobCreateRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentImportJobPageQuery;

import java.time.LocalDateTime;

public interface ContentImportJobRepository {

    PageResult<ContentImportJob> page(ContentImportJobPageQuery query);

    ContentImportJob findById(Long id);

    ContentImportJob create(ContentImportJobCreateRequest request, Long tenantId, Long createBy);

    ContentImportJob pollPending();

    boolean markRunning(Long jobId);

    void markSuccess(Long jobId, int totalCount, int successCount, int failCount, String resultJson);

    void markFailed(Long jobId, String message);

    long countByTenantAndTimeRange(Long tenantId, LocalDateTime start, LocalDateTime end);

    /**
     * 按状态统计导入任务数量。
     *
     * @param tenantId 租户 ID，可为 null
     * @param status   任务状态
     * @return 任务数量
     */
    long countByStatus(Long tenantId, String status);
}
