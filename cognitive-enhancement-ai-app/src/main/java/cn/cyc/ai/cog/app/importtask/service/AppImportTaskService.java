package cn.cyc.ai.cog.app.importtask.service;

import cn.cyc.ai.cog.app.contract.AppPageQuery;
import cn.cyc.ai.cog.app.contract.AppPageVO;
import cn.cyc.ai.cog.app.importtask.dto.AppImportTaskCreateRequest;
import cn.cyc.ai.cog.app.importtask.dto.AppImportTaskVO;
import cn.cyc.ai.cog.app.importtask.support.AppImportTaskExecutor;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.knowledge.process.ImportBizType;
import cn.cyc.ai.cog.platform.file.spi.PlatformFileClient;
import cn.cyc.ai.cog.platform.importtask.entity.ImportTaskEntity;
import cn.cyc.ai.cog.platform.importtask.spi.ImportTaskPersistencePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 导入任务 CRUD 与状态查询（流水线委托 {@link cn.cyc.ai.cog.app.importtask.support.AppImportTaskExecutor}）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Service
public class AppImportTaskService {

    private final ImportTaskPersistencePort importTaskPersistence;
    private final AppImportTaskExecutor importTaskExecutor;
    private final ObjectMapper objectMapper;

    public AppImportTaskService(ImportTaskPersistencePort importTaskPersistence,
                                AppImportTaskExecutor importTaskExecutor,
                                ObjectMapper objectMapper) {
        this.importTaskPersistence = importTaskPersistence;
        this.importTaskExecutor = importTaskExecutor;
        this.objectMapper = objectMapper;
    }

    public AppImportTaskVO create(AppImportTaskCreateRequest request) {
        Long userId = requireUserId();
        if (request == null || !StringUtils.hasText(request.getChannel())) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "channel 不能为空");
        }
        if (request.getFileId() == null && !StringUtils.hasText(request.getFileUrl())) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "fileId 或 fileUrl 不能为空");
        }
        if (!StringUtils.hasText(request.getImportBizType())) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "importBizType 不能为空");
        }
        ImportBizType bizType = ImportBizType.fromCode(request.getImportBizType());
        ImportTaskEntity entity = new ImportTaskEntity();
        entity.setTenantId(TenantContext.currentTenantId());
        entity.setUserId(userId);
        entity.setTaskCode("import-" + System.currentTimeMillis());
        entity.setImportBizType(bizType.name());
        entity.setChannel(request.getChannel());
        entity.setFileId(request.getFileId());
        entity.setFileUrl(resolveFileUrl(request));
        entity.setTitle(StringUtils.hasText(request.getTitle()) ? request.getTitle() : request.getFileName());
        entity.setFileName(request.getFileName());
        entity.setTargetType(request.getTargetType());
        entity.setTagsJson(writeTags(request.getTags()));
        entity.setAiEnhanced(Boolean.TRUE.equals(request.getAiEnhanced()));
        entity.setAutoQuiz(Boolean.TRUE.equals(request.getAutoQuiz()));
        entity.setStatus("pending");
        entity.setStage("pending");
        entity.setProgress(0);
        importTaskPersistence.save(entity);
        importTaskExecutor.runPipeline(entity.getTenantId(), entity.getUserId(), entity.getTaskCode());
        return toVo(entity);
    }

    public AppPageVO<AppImportTaskVO> page(AppPageQuery query) {
        AppPageQuery body = query == null ? new AppPageQuery() : query;
        Long userId = requireUserId();
        return AppPageVO.from(importTaskPersistence.page(
                TenantContext.currentTenantId(), userId, body.resolvePage(), body.resolveSize()), this::toVo);
    }

    public AppImportTaskVO detail(String id) {
        Long userId = requireUserId();
        ImportTaskEntity entity = findOwned(id, userId);
        return toVo(entity);
    }

    public AppImportTaskVO retry(String id) {
        Long userId = requireUserId();
        ImportTaskEntity entity = findOwned(id, userId);
        entity.setStatus("pending");
        entity.setStage("pending");
        entity.setProgress(0);
        entity.setErrorMessage(null);
        importTaskPersistence.update(entity);
        importTaskExecutor.runPipeline(entity.getTenantId(), entity.getUserId(), entity.getTaskCode());
        return toVo(entity);
    }

    public Map<String, Object> latestActiveStatus() {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            return null;
        }
        Optional<ImportTaskEntity> active = importTaskPersistence.findLatestActive(
                TenantContext.currentTenantId(), userId);
        return active.map(entity -> {
            Map<String, Object> status = new HashMap<>();
            status.put("fileName", entity.getFileName());
            status.put("progress", entity.getProgress());
            status.put("status", entity.getStatus());
            status.put("meta", "正在" + entity.getStage() + "...");
            return status;
        }).orElse(null);
    }

    private ImportTaskEntity findOwned(String id, Long userId) {
        try {
            return importTaskPersistence.findById(TenantContext.currentTenantId(), userId, Long.parseLong(id))
                    .orElseThrow(() -> Errors.of(PlatformErrorCode.NOT_FOUND, "导入任务不存在"));
        } catch (NumberFormatException ex) {
            return importTaskPersistence.findByCode(TenantContext.currentTenantId(), userId, id)
                    .orElseThrow(() -> Errors.of(PlatformErrorCode.NOT_FOUND, "导入任务不存在"));
        }
    }

    private AppImportTaskVO toVo(ImportTaskEntity entity) {
        AppImportTaskVO vo = new AppImportTaskVO();
        vo.setId(entity.getTaskCode());
        vo.setTitle(entity.getTitle());
        vo.setStatus(entity.getStatus());
        vo.setStage(entity.getStage());
        vo.setProgress(entity.getProgress());
        vo.setFileName(entity.getFileName());
        vo.setError(entity.getErrorMessage());
        vo.setLibraryItemId(entity.getLibraryItemId() == null ? null : String.valueOf(entity.getLibraryItemId()));
        return vo;
    }

    private String writeTags(List<String> tags) {
        try {
            return objectMapper.writeValueAsString(tags == null ? List.of() : tags);
        } catch (Exception ex) {
            return "[]";
        }
    }

    private String resolveFileUrl(AppImportTaskCreateRequest request) {
        if (StringUtils.hasText(request.getFileUrl())) {
            return request.getFileUrl().trim();
        }
        if (request.getFileId() != null) {
            return PlatformFileClient.toBaseFileUrl(request.getFileId());
        }
        return null;
    }

    private Long requireUserId() {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            throw Errors.of(PlatformErrorCode.UNAUTHORIZED);
        }
        return userId;
    }
}
