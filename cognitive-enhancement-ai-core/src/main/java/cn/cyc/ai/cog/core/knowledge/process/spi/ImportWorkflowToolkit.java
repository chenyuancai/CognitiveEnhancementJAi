package cn.cyc.ai.cog.core.knowledge.process.spi;

import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;

/**
 * 导入流水线工具集（由 runtime 装配具体实现）。
 */
public interface ImportWorkflowToolkit {

    void resolveFile(ImportWorkflowState state);

    void fileToHtml(ImportWorkflowState state);

    void htmlToMarkdown(ImportWorkflowState state);

    void buildParsedDocument(ImportWorkflowState state);

    void buildChunks(ImportWorkflowState state);

    void vectorizeChunks(ImportWorkflowState state);

    Long persistContent(ImportWorkflowState state);

    void enrichWithAi(ImportWorkflowState state);
}
