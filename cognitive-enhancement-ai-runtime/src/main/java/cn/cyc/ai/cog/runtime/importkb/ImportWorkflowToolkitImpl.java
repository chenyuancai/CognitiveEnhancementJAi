package cn.cyc.ai.cog.runtime.importkb;

import cn.cyc.ai.cog.core.knowledge.process.spi.ImportKnowledgePersistPort;
import cn.cyc.ai.cog.core.knowledge.process.spi.ImportWorkflowToolkit;
import cn.cyc.ai.cog.core.knowledge.process.spi.ImportAiPort;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import cn.cyc.ai.cog.runtime.importkb.tool.KbChunkBuildTool;
import cn.cyc.ai.cog.runtime.importkb.tool.KbFileResolveTool;
import cn.cyc.ai.cog.runtime.importkb.tool.KbFileToHtmlTool;
import cn.cyc.ai.cog.runtime.importkb.tool.KbHtmlToMarkdownTool;
import cn.cyc.ai.cog.runtime.importkb.tool.KbParsedDocumentBuilderTool;
import cn.cyc.ai.cog.runtime.importkb.tool.KbVectorizeTool;
import org.springframework.stereotype.Component;

/**
 * 导入工作流工具集装配。
 */
@Component
public class ImportWorkflowToolkitImpl implements ImportWorkflowToolkit {

    private final KbFileResolveTool fileResolveTool;
    private final KbFileToHtmlTool fileToHtmlTool;
    private final KbHtmlToMarkdownTool htmlToMarkdownTool;
    private final KbParsedDocumentBuilderTool parsedDocumentBuilderTool;
    private final KbChunkBuildTool chunkBuildTool;
    private final KbVectorizeTool vectorizeTool;
    private final ImportKnowledgePersistPort knowledgePersistPort;
    private final ImportAiPort importAiPort;

    public ImportWorkflowToolkitImpl(KbFileResolveTool fileResolveTool,
                                     KbFileToHtmlTool fileToHtmlTool,
                                     KbHtmlToMarkdownTool htmlToMarkdownTool,
                                     KbParsedDocumentBuilderTool parsedDocumentBuilderTool,
                                     KbChunkBuildTool chunkBuildTool,
                                     KbVectorizeTool vectorizeTool,
                                     ImportKnowledgePersistPort knowledgePersistPort,
                                     ImportAiPort importAiPort) {
        this.fileResolveTool = fileResolveTool;
        this.fileToHtmlTool = fileToHtmlTool;
        this.htmlToMarkdownTool = htmlToMarkdownTool;
        this.parsedDocumentBuilderTool = parsedDocumentBuilderTool;
        this.chunkBuildTool = chunkBuildTool;
        this.vectorizeTool = vectorizeTool;
        this.knowledgePersistPort = knowledgePersistPort;
        this.importAiPort = importAiPort;
    }

    @Override
    public void resolveFile(ImportWorkflowState state) {
        fileResolveTool.resolve(state);
    }

    @Override
    public void fileToHtml(ImportWorkflowState state) {
        fileToHtmlTool.toHtml(state);
    }

    @Override
    public void htmlToMarkdown(ImportWorkflowState state) {
        htmlToMarkdownTool.toMarkdown(state);
    }

    @Override
    public void buildParsedDocument(ImportWorkflowState state) {
        parsedDocumentBuilderTool.build(state);
    }

    @Override
    public void buildChunks(ImportWorkflowState state) {
        chunkBuildTool.buildChunks(state);
    }

    @Override
    public void vectorizeChunks(ImportWorkflowState state) {
        vectorizeTool.vectorize(state);
    }

    @Override
    public Long persistContent(ImportWorkflowState state) {
        return knowledgePersistPort.persist(state);
    }

    @Override
    public void enrichWithAi(ImportWorkflowState state) {
        if (state.getParsedDocument() == null) {
            return;
        }
        String summary = importAiPort.summarize(state.getParsedDocument().title(), state.getMarkdown());
        state.setSummary(summary);
    }
}
