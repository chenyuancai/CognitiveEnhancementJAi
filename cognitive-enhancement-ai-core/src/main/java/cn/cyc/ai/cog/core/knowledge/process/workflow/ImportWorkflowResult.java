package cn.cyc.ai.cog.core.knowledge.process.workflow;

/**
 * 导入工作流执行结果。
 */
public record ImportWorkflowResult(
        Long contentId,
        int chunkCount,
        boolean vectorized,
        boolean aiEnriched
) {
}
