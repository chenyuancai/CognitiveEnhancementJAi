package cn.cyc.ai.cog.runtime.coordinator;

/**
 * 多 Agent 执行策略。
 *
 * @author cyc
 */
public enum ExecutionStrategy {

    COST_FIRST,
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
