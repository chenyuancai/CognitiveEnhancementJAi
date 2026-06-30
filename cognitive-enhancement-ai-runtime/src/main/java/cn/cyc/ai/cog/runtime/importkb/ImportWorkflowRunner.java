package cn.cyc.ai.cog.runtime.importkb;

import cn.cyc.ai.cog.core.knowledge.process.ImportBizType;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportFileParseWorkflowExecutor;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowListener;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowResult;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入工作流同步执行器（admin 调试与 Harness 步骤复用）。
 */
@Service
public class ImportWorkflowRunner {

    private final ImportFileParseWorkflowExecutor workflowExecutor;

    public ImportWorkflowRunner(ImportFileParseWorkflowExecutor workflowExecutor) {
        this.workflowExecutor = workflowExecutor;
    }

    /**
     * 同步执行导入流水线并收集阶段日志。
     */
    public ImportWorkflowRunResult run(ImportWorkflowState state) {
        return run(state, null);
    }

    /**
     * 同步执行导入流水线，支持阶段回调（SSE / 任务进度）。
     */
    public ImportWorkflowRunResult run(ImportWorkflowState state, ImportWorkflowListener listener) {
        List<ImportWorkflowStageLog> stages = new ArrayList<>();
        ImportWorkflowListener composite = (stage, message) -> {
            stages.add(new ImportWorkflowStageLog(stage.name(), stage.clientStage(), stage.progressHint(), message));
            if (listener != null) {
                listener.onStage(stage, message);
            }
        };
        try {
            ImportWorkflowResult result = workflowExecutor.execute(state, composite);
            String markdownPreview = abbreviate(state.getMarkdown(), 800);
            return new ImportWorkflowRunResult(result, List.copyOf(stages), markdownPreview);
        } finally {
            cleanupTempFile(state);
        }
    }

    /**
     * 构造工作流上下文。
     */
    public static ImportWorkflowState buildState(Long tenantId,
                                                 Long userId,
                                                 String taskCode,
                                                 ImportBizType importBizType,
                                                 Long fileId,
                                                 String fileUrl,
                                                 String fileName,
                                                 String title,
                                                 boolean aiEnhanced,
                                                 boolean autoQuiz) {
        ImportWorkflowState state = new ImportWorkflowState();
        state.setTenantId(tenantId == null ? 1L : tenantId);
        state.setUserId(userId == null ? 0L : userId);
        state.setTaskCode(StringUtils.hasText(taskCode) ? taskCode : "import-debug-" + System.currentTimeMillis());
        state.setImportBizType(importBizType == null ? ImportBizType.KNOWLEDGE_DOCUMENT : importBizType);
        state.setChannel("file");
        state.setFileId(fileId);
        state.setFileUrl(fileUrl);
        state.setFileName(fileName);
        state.setTitle(title);
        state.setAiEnhanced(aiEnhanced);
        state.setAutoQuiz(autoQuiz);
        return state;
    }

    private void cleanupTempFile(ImportWorkflowState state) {
        if (state.getLocalFilePath() == null) {
            return;
        }
        try {
            Files.deleteIfExists(state.getLocalFilePath());
        } catch (Exception ignored) {
            // best effort
        }
    }

    private String abbreviate(String text, int max) {
        if (!StringUtils.hasText(text) || text.length() <= max) {
            return text == null ? "" : text;
        }
        return text.substring(0, max);
    }

    public record ImportWorkflowStageLog(
            String stage,
            String clientStage,
            int progress,
            String message
    ) {
    }

    public record ImportWorkflowRunResult(
            ImportWorkflowResult workflow,
            List<ImportWorkflowStageLog> stages,
            String markdownPreview
    ) {
    }
}
