package cn.cyc.ai.cog.app.tutoring.strategy;

import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringCachedMessage;
import cn.cyc.ai.cog.app.tutoring.context.AppTutoringResolvedContext;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringLlmAnalysisResult;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;

import java.util.List;

/**
 * 教学策略决策输入，聚合本轮消息、会话状态与 LLM 分析结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AppTutoringStrategyInput(
        String message,
        List<AppTutoringCachedMessage> recentMessages,
        AppTutoringStudentState studentState,
        AppTutoringResolvedContext resolvedContext,
        int stuckFallbackThreshold,
        AppLearningProfile profile,
        AppTutoringLlmAnalysisResult llmAnalysis) {

    /**
     * 构建不含画像与 LLM 分析的基础决策输入。
     *
     * @param message                用户本轮消息
     * @param recentMessages         最近会话消息
     * @param studentState           学生学习状态
     * @param resolvedContext        解析后的引用上下文
     * @param stuckFallbackThreshold 卡住次数触发分步讲解的阈值
     * @return 基础策略决策输入
     */
    public static AppTutoringStrategyInput basic(String message,
                                                 List<AppTutoringCachedMessage> recentMessages,
                                                 AppTutoringStudentState studentState,
                                                 AppTutoringResolvedContext resolvedContext,
                                                 int stuckFallbackThreshold) {
        return new AppTutoringStrategyInput(
                message,
                recentMessages,
                studentState,
                resolvedContext,
                stuckFallbackThreshold,
                null,
                AppTutoringLlmAnalysisResult.empty());
    }
}
