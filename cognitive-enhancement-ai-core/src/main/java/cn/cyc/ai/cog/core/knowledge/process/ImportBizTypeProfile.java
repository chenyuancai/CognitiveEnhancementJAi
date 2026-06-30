package cn.cyc.ai.cog.core.knowledge.process;

/**
 * 各业务类型的流水线阶段开关。
 */
public record ImportBizTypeProfile(
        boolean vectorize,
        boolean aiSummary,
        boolean autoQuiz
) {
}
