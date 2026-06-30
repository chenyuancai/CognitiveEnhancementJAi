package cn.cyc.ai.cog.app.tutoring.strategy;

/**
 * 知识点掌握等级枚举，用于画像与会话学习状态表达。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum AppMasteryLevel {

    /** 尚未评估。 */
    UNKNOWN,

    /** 需要补学。 */
    NEEDS_REMEDIAL,

    /** 正在提升。 */
    IMPROVING,

    /** 可进入练习。 */
    PRACTICE_READY,

    /** 已掌握。 */
    MASTERED
}
