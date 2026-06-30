package cn.cyc.ai.cog.app.tutoring.dto;

/**
 * LLM 对学生作答的结构化分析结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AppTutoringLlmAnalysisResult(
        String intent,
        String reasoningJudgment,
        String knowledgePoint,
        String mistakeSummary,
        double confidence
) {

    /**
     * 返回空分析结果占位。
     *
     * @return 各字段为默认值的分析结果
     */
    public static AppTutoringLlmAnalysisResult empty() {
        return new AppTutoringLlmAnalysisResult("", "", "", "", 0.0);
    }
}
