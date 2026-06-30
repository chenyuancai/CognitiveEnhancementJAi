package cn.cyc.ai.cog.app.tutoring.strategy;

/**
 * C 端学习辅导教学策略枚举，决定本轮回答的教学动作类型。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum AppTeachingStrategy {

    /** 追问引导。 */
    SOCRATIC_QUESTIONING,

    /** 先提示再追问。 */
    HINT_THEN_QUESTION,

    /** 分步讲解。 */
    STEP_BY_STEP_EXPLANATION,

    /** 直接回答。 */
    DIRECT_ANSWER,

    /** 补前置知识。 */
    REMEDIAL_TEACHING,

    /** 练习检查。 */
    PRACTICE_CHECK,

    /** 总结复习。 */
    SUMMARY_REVIEW,

    /** 学习规划。 */
    LEARNING_PLAN
}
