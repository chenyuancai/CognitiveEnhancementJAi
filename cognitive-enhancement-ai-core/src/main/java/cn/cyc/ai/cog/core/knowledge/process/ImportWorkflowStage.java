package cn.cyc.ai.cog.core.knowledge.process;

/**
 * 导入工作流内部阶段（映射到 C 端 SSE 的 parsing/normalizing/indexing/enriching）。
 */
public enum ImportWorkflowStage {

    FILE_RESOLVE("parsing", 5),
    FILE_TO_HTML("parsing", 15),
    HTML_TO_MARKDOWN("normalizing", 30),
    BUILD_PARSED_DOCUMENT("normalizing", 45),
    BUILD_CHUNKS("indexing", 60),
    VECTORIZE_CHUNKS("indexing", 75),
    PERSIST_CONTENT("enriching", 85),
    AI_ENRICH("enriching", 92),
    FINISH("done", 100);

    private final String clientStage;
    private final int progressHint;

    ImportWorkflowStage(String clientStage, int progressHint) {
        this.clientStage = clientStage;
        this.progressHint = progressHint;
    }

    public String clientStage() {
        return clientStage;
    }

    public int progressHint() {
        return progressHint;
    }
}
