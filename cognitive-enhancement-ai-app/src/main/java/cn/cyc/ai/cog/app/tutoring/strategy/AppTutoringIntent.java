package cn.cyc.ai.cog.app.tutoring.strategy;

/**
 * C 端学习辅导意图分类枚举，标识用户本轮问题的学习诉求类型。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum AppTutoringIntent {

    /** 概念解释。 */
    CONCEPT_EXPLANATION,

    /** 解题辅导。 */
    PROBLEM_SOLVING,

    /** 错题分析。 */
    MISTAKE_ANALYSIS,

    /** 资料总结复习。 */
    SUMMARY_REVIEW,

    /** 学习规划。 */
    LEARNING_PLAN,

    /** 事实问答。 */
    FACT_QA,

    /** 普通闲聊。 */
    FREE_CHAT
}
