package cn.cyc.ai.cog.runtime.coordinator;

/**
 * 多 Agent 执行策略。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum ExecutionStrategy {

    /** costfirst。 */
    COST_FIRST,
    /** qualityfirst。 */
    QUALITY_FIRST,
    BALANCED;

    /**
     * 解析策略编码，未知值回退 BALANCED。
     *
     * @param raw 原始策略值
     * @return 执行策略
     */
    public static ExecutionStrategy from(String raw) {
        if (raw == null || raw.isBlank()) {
            return BALANCED;
        }
        try {
            return ExecutionStrategy.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return BALANCED;
        }
    }
}
