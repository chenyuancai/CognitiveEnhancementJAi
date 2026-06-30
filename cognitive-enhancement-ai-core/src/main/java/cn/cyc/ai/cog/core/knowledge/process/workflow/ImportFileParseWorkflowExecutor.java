package cn.cyc.ai.cog.core.knowledge.process.workflow;

import cn.cyc.ai.cog.core.knowledge.process.ImportBizTypeProfiles;
import cn.cyc.ai.cog.core.knowledge.process.ImportWorkflowStage;
import cn.cyc.ai.cog.core.knowledge.process.spi.ImportWorkflowToolkit;

import java.util.Objects;

/**
 * 知识文件解析工作流执行器（纯 Java 节点编排，无 Spring 依赖）。
 */
public class ImportFileParseWorkflowExecutor {

    private final ImportWorkflowToolkit toolkit;

    public ImportFileParseWorkflowExecutor(ImportWorkflowToolkit toolkit) {
        this.toolkit = Objects.requireNonNull(toolkit, "toolkit");
    }

    /**
     * 执行完整导入流水线。
     */
    public ImportWorkflowResult execute(ImportWorkflowState state, ImportWorkflowListener listener) {
        Objects.requireNonNull(state, "state");
        ImportWorkflowListener safeListener = listener == null ? (stage, message) -> {
        } : listener;
        var profile = ImportBizTypeProfiles.of(state.getImportBizType());

        runStage(ImportWorkflowStage.FILE_RESOLVE, safeListener, () -> toolkit.resolveFile(state));
        runStage(ImportWorkflowStage.FILE_TO_HTML, safeListener, () -> toolkit.fileToHtml(state));
        runStage(ImportWorkflowStage.HTML_TO_MARKDOWN, safeListener, () -> toolkit.htmlToMarkdown(state));
        runStage(ImportWorkflowStage.BUILD_PARSED_DOCUMENT, safeListener, () -> toolkit.buildParsedDocument(state));
        runStage(ImportWorkflowStage.BUILD_CHUNKS, safeListener, () -> toolkit.buildChunks(state));

        boolean vectorized = false;
        if (profile.vectorize()) {
            runStage(ImportWorkflowStage.VECTORIZE_CHUNKS, safeListener, () -> toolkit.vectorizeChunks(state));
            vectorized = true;
        }

        runStage(ImportWorkflowStage.PERSIST_CONTENT, safeListener, () -> state.setContentId(toolkit.persistContent(state)));

        boolean aiEnriched = false;
        if (state.isAiEnhanced() && profile.aiSummary()) {
            runStage(ImportWorkflowStage.AI_ENRICH, safeListener, () -> toolkit.enrichWithAi(state));
            aiEnriched = true;
        }

        safeListener.onStage(ImportWorkflowStage.FINISH, "导入完成");
        return new ImportWorkflowResult(
                state.getContentId(),
                state.getChunks().size(),
                vectorized,
                aiEnriched);
    }

    private void runStage(ImportWorkflowStage stage, ImportWorkflowListener listener, Runnable action) {
        listener.onStage(stage, stage.name());
        action.run();
    }
}
