package cn.cyc.ai.cog.runtime.trace.domain;

/**
 * TraceSpan 步骤类型。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum TraceSpanType {

    /** 能力。 */
    CAPABILITY,
    /** 智能体。 */
    AGENT,
    /** 工具。 */
    TOOL,
    /** llm。 */
    LLM,
    /** policy。 */
    POLICY,
    /** 额度。 */
    QUOTA,
    /** 计划。 */
    PLAN,
    /** delegate。 */
    DELEGATE,
    REFLECTION
}
