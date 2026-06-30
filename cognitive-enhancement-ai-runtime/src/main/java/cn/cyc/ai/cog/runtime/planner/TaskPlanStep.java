package cn.cyc.ai.cog.runtime.planner;

/**
 * 任务规划步骤。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record TaskPlanStep(int order, String action, String description, String status, String toolCode) {

    /**
     * 兼容无 toolCode 的构造。
     */
    public TaskPlanStep(int order, String action, String description, String status) {
        this(order, action, description, status, null);
    }
}
