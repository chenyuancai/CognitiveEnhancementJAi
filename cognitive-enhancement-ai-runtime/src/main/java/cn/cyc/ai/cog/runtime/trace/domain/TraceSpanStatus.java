package cn.cyc.ai.cog.runtime.trace.domain;

/**
 * TraceSpan 步骤状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum TraceSpanStatus {

    /** 成功。 */
    SUCCESS,
    /** failed。 */
    FAILED,
    SKIPPED
}
