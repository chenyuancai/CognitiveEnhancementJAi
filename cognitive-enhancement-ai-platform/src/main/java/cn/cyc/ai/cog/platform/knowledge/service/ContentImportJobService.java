package cn.cyc.ai.cog.platform.knowledge.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.file.spi.PlatformFileClient;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentImportJob;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentImportJobCreateRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentImportJobPageQuery;
import cn.cyc.ai.cog.platform.knowledge.repository.ContentImportJobRepository;
import cn.cyc.ai.cog.platform.knowledge.repository.ContentRepository;
import cn.cyc.ai.cog.platform.knowledge.support.ContentImportCsvSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContentImportJobService {

    private static final Logger log = LoggerFactory.getLogger(ContentImportJobService.class);
    private static final String IMPORT_BIZ_CODE = "content-import";
    private static final String CSV_CONTENT_TYPE = "text/csv";

    private final ContentImportJobRepository contentImportJobRepository;
    private final ContentRepository contentRepository;
    private final PlatformFileClient platformFileClient;

    public ContentImportJobService(ContentImportJobRepository contentImportJobRepository,
                                   ContentRepository contentRepository,
                                   PlatformFileClient platformFileClient) {
        this.contentImportJobRepository = contentImportJobRepository;
        this.contentRepository = contentRepository;
        this.platformFileClient = platformFileClient;
    }

    public PageResult<ContentImportJob> page(ContentImportJobPageQuery query) {
        return contentImportJobRepository.page(query);
    }

    public ContentImportJob detail(Long id) {
        return contentImportJobRepository.findById(id);
    }

    public ContentImportJob create(ContentImportJobCreateRequest request) {
        Long tenantId = TenantContext.currentTenantId();
        resolveFileReference(request, tenantId);
        ContentImportJob job = contentImportJobRepository.create(
                request, tenantId, UserContext.currentUserId());
        if (PlatformFileClient.isBaseFileUrl(job.fileUrl())) {
            platformFileClient.ensure(List.of(PlatformFileClient.parseBaseFileId(job.fileUrl())));
        }
        return job;
    }

    public long countByTenantAndTimeRange(Long tenantId, LocalDateTime start, LocalDateTime end) {
        return contentImportJobRepository.countByTenantAndTimeRange(tenantId, start, end);
    }

    /** 轮询并处理一条 PENDING 导入任务（由 admin 定时任务调用）。 */
    public void processNextPendingJob() {
        ContentImportJob job = contentImportJobRepository.pollPending();
        if (job == null) {
            return;
        }
        if (!contentImportJobRepository.markRunning(job.id())) {
            return;
        }
        try {
            processJob(job);
        } catch (Exception ex) {
            log.warn("内容导入任务失败，jobId={}", job.id(), ex);
            contentImportJobRepository.markFailed(job.id(), ex.getMessage());
        }
    }

    private void resolveFileReference(ContentImportJobCreateRequest request, Long tenantId) {
        if (request.getFileId() != null) {
            platformFileClient.getById(request.getFileId());
            request.setFileUrl(PlatformFileClient.toBaseFileUrl(request.getFileId()));
            request.setFileContent(null);
            return;
        }
        if (StringUtils.hasText(request.getFileContent())) {
            Long fileId = platformFileClient.uploadText(
                    tenantId,
                    IMPORT_BIZ_CODE,
                    request.getFileName().trim(),
                    request.getFileContent(),
                    CSV_CONTENT_TYPE);
            request.setFileUrl(PlatformFileClient.toBaseFileUrl(fileId));
            request.setFileContent(null);
            return;
        }
        if (!StringUtils.hasText(request.getFileUrl())) {
            throw Errors.of(PlatformErrorCode.CONTENT_IMPORT_SOURCE_REQUIRED);
        }
    }

    private void processJob(ContentImportJob job) {
        if (!StringUtils.hasText(job.fileName())) {
            throw Errors.of(PlatformErrorCode.CONTENT_IMPORT_FILENAME_EMPTY);
        }
        String csv = resolveCsvContent(job);
        if (!StringUtils.hasText(csv)) {
            throw Errors.of(PlatformErrorCode.CONTENT_IMPORT_CSV_EMPTY);
        }
        ContentImportCsvSupport.ParseResult parsed = ContentImportCsvSupport.parse(csv);
        for (ContentImportCsvSupport.RowResult row : parsed.rows()) {
            if (row.success()) {
                contentRepository.createImportDraft(job.tenantId(), row.request());
            }
        }
        contentImportJobRepository.markSuccess(
                job.id(),
                parsed.rows().size(),
                parsed.successCount(),
                parsed.failCount(),
                parsed.toResultJson());
        log.info("内容导入任务完成，jobId={}, success={}, fail={}",
                job.id(), parsed.successCount(), parsed.failCount());
    }

    private String resolveCsvContent(ContentImportJob job) {
        if (StringUtils.hasText(job.sourceContent())) {
            return job.sourceContent();
        }
        if (PlatformFileClient.isBaseFileUrl(job.fileUrl())) {
            return platformFileClient.readText(PlatformFileClient.parseBaseFileId(job.fileUrl()));
        }
        return null;
    }
}
