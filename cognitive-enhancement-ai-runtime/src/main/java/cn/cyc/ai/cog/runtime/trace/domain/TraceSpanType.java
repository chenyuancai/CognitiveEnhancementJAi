package cn.cyc.ai.cog.runtime.trace.domain;

/**
 * TraceSpan 步骤类型。
 *
 * @author cyc
 */
public enum TraceSpanType {

    CAPABILITY,
    AGENT,
    TOOL,
    LLM,
    POLICY,
    QUOTA,
    PLAN,
    DELEGATE,
    REFLECTION
}
