package cn.cyc.ai.cog.app.tutoring.post;

import cn.cyc.ai.cog.app.tutoring.context.AppTutoringResolvedContext;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringBlueprint;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringGovernanceResult;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringLlmAnalysisResult;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyDecision;

/**
 * 单轮辅导后处理上下文，携带本轮对话与决策所需的全部状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AppTutoringAfterTurnContext(
        Long userId,
        String sessionId,
        String traceId,
        String messageId,
        String message,
        String answer,
        boolean newSession,
        AppTutoringStudentState studentState,
        AppTutoringStrategyDecision decision,
        AppTutoringBlueprint blueprint,
        AppTutoringResolvedContext resolvedContext,
        AppTutoringLlmAnalysisResult llmAnalysis,
        AppTutoringGovernanceResult governanceResult) {
}
