package cn.cyc.ai.cog.app.importtask.service;

import cn.cyc.ai.cog.core.knowledge.process.ImportBizType;
import cn.cyc.ai.cog.core.knowledge.process.ImportWorkflowStage;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowResult;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import cn.cyc.ai.cog.platform.importtask.entity.ImportTaskEntity;
import cn.cyc.ai.cog.platform.importtask.spi.ImportTaskPersistencePort;
import cn.cyc.ai.cog.runtime.importkb.ImportWorkflowRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 导入工作流业务编排：加载任务、执行 core 流水线、回写状态与 SSE。
 */
@Service
public class AppImportWorkflowBusinessService {

    private static final Logger log = LoggerFactory.getLogger(AppImportWorkflowBusinessService.class);

    private final ImportTaskPersistencePort importTaskPersistence;
    private final ImportWorkflowRunner importWorkflowRunner;
    private final ObjectMapper objectMapper;

    public AppImportWorkflowBusinessService(ImportTaskPersistencePort importTaskPersistence,
                                            ImportWorkflowRunner importWorkflowRunner,
                                            ObjectMapper objectMapper) {
        this.importTaskPersistence = importTaskPersistence;
        this.importWorkflowRunner = importWorkflowRunner;
        this.objectMapper = objectMapper;
    }

    public void execute(Long tenantId, Long userId, String taskCode, Consumer<Map<String, Object>> progressSink) {
        ImportTaskEntity entity = importTaskPersistence.findByCode(tenantId, userId, taskCode).orElse(null);
        if (entity == null) {
            return;
        }
        entity.setStatus("processing");
        entity.setStage(ImportWorkflowStage.FILE_RESOLVE.clientStage());
        entity.setProgress(ImportWorkflowStage.FILE_RESOLVE.progressHint());
        importTaskPersistence.update(entity);
        publish(progressSink, entity, "progress");

        ImportWorkflowState state = toState(entity);
        try {
            ImportWorkflowRunner.ImportWorkflowRunResult outcome = importWorkflowRunner.run(state, (stage, message) -> {
                entity.setStage(stage.clientStage());
                entity.setProgress(stage.progressHint());
                importTaskPersistence.update(entity);
                publish(progressSink, entity, "progress");
            });
            var result = outcome.workflow();
            entity.setStatus("done");
            entity.setStage("done");
            entity.setProgress(100);
            entity.setLibraryItemId(result.contentId());
            entity.setErrorMessage(null);
            entity.setResultJson(writeResult(result));
            importTaskPersistence.update(entity);
            Map<String, Object> done = new HashMap<>();
            done.put("type", "done");
            done.put("taskId", entity.getTaskCode());
            done.put("itemIds", List.of(String.valueOf(result.contentId())));
            done.put("libraryItemId", result.contentId());
            if (progressSink != null) {
                progressSink.accept(done);
            }
        } catch (Exception ex) {
            log.warn("import workflow failed taskCode={}", taskCode, ex);
            entity.setStatus("failed");
            entity.setErrorMessage(ex.getMessage());
            importTaskPersistence.update(entity);
            Map<String, Object> failed = new HashMap<>();
            failed.put("type", "error");
            failed.put("taskId", entity.getTaskCode());
            failed.put("message", ex.getMessage());
            if (progressSink != null) {
                progressSink.accept(failed);
            }
        }
    }

    private ImportWorkflowState toState(ImportTaskEntity entity) {
        ImportWorkflowState state = new ImportWorkflowState();
        state.setTenantId(entity.getTenantId());
        state.setUserId(entity.getUserId());
        state.setTaskCode(entity.getTaskCode());
        state.setImportBizType(ImportBizType.fromCode(entity.getImportBizType()));
        state.setChannel(entity.getChannel());
        state.setFileId(entity.getFileId());
        state.setFileUrl(entity.getFileUrl());
        state.setFileName(entity.getFileName());
        state.setTitle(entity.getTitle());
        state.setAiEnhanced(Boolean.TRUE.equals(entity.getAiEnhanced()));
        state.setAutoQuiz(Boolean.TRUE.equals(entity.getAutoQuiz()));
        return state;
    }

    private void publish(Consumer<Map<String, Object>> progressSink, ImportTaskEntity entity, String type) {
        if (progressSink == null) {
            return;
        }
        Map<String, Object> event = new HashMap<>();
        event.put("type", type);
        event.put("taskId", entity.getTaskCode());
        event.put("stage", entity.getStage());
        event.put("progress", entity.getProgress());
        progressSink.accept(event);
    }

    private String writeResult(ImportWorkflowResult result) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("contentId", result.contentId());
            payload.put("chunkCount", result.chunkCount());
            payload.put("vectorized", result.vectorized());
            payload.put("aiEnriched", result.aiEnriched());
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            return "{}";
        }
    }
}
