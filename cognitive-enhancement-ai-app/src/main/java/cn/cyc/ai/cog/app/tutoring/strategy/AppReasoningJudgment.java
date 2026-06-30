package cn.cyc.ai.cog.app.tutoring.strategy;

/**
 * 学生推理过程正误判断枚举，由 LLM 分析或规则推断产出。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum AppReasoningJudgment {

    /** 推理正确。 */
    CORRECT,

    /** 推理错误。 */
    INCORRECT,

    /** 无法判断。 */
    UNKNOWN
}
