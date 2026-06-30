package cn.cyc.ai.cog.runtime.importkb.tool;

import cn.cyc.ai.cog.core.knowledge.process.model.KbContentChunk;
import cn.cyc.ai.cog.core.knowledge.process.spi.ImportAiPort;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 分块向量化（经 KbImportAiService / RuntimeHarness）。
 */
@Component
public class KbVectorizeTool {

    private final ImportAiPort importAiPort;

    public KbVectorizeTool(ImportAiPort importAiPort) {
        this.importAiPort = importAiPort;
    }

    public void vectorize(ImportWorkflowState state) {
        List<KbContentChunk> vectorized = new ArrayList<>();
        for (KbContentChunk chunk : state.getChunks()) {
            List<Float> embedding = importAiPort.embed(chunk.chunkText());
            vectorized.add(new KbContentChunk(
                    chunk.chunkIndex(),
                    chunk.chunkText(),
                    chunk.headingPath(),
                    embedding));
        }
        state.getChunks().clear();
        state.getChunks().addAll(vectorized);
    }
}
