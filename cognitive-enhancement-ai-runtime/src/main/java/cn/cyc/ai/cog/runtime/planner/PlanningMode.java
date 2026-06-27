package cn.cyc.ai.cog.runtime.planner;

/**
 * 任务规划模式。
 *
 * @author cyc
 */
public enum PlanningMode {

    RULE,
    LLM;

    /**
     * 解析规划模式，未知值回退 RULE。
     *
     * @param raw 原始值
     * @return 规划模式
     */
    public static PlanningMode from(Object raw) {
        if (raw == null) {
            return RULE;
        }
        try {
            return PlanningMode.valueOf(String.valueOf(raw).trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return RULE;
        }
    }
}
