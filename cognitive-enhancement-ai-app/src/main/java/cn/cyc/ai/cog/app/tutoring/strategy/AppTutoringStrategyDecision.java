package cn.cyc.ai.cog.app.tutoring.strategy;

/**
 * 教学策略决策结果，包含意图、策略及下一步动作指引。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AppTutoringStrategyDecision(
        AppTutoringIntent intent,
        AppTeachingStrategy strategy,
        String reason,
        String nextActionType,
        boolean needUserReply
) {
}
