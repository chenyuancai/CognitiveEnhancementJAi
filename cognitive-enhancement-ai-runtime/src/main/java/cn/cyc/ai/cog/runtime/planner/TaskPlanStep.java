package cn.cyc.ai.cog.runtime.planner;

/**
 * 任务规划步骤。
 *
 * @param order       步骤序号
 * @param action      动作编码
 * @param description 步骤描述
 * @param status      步骤状态
 * @param toolCode    建议使用的 Tool 编码（可选）
 * @author cyc
 */
public record TaskPlanStep(int order, String action, String description, String status, String toolCode) {

    /**
     * 兼容无 toolCode 的构造。
     */
    public TaskPlanStep(int order, String action, String description, String status) {
        this(order, action, description, status, null);
    }
}
