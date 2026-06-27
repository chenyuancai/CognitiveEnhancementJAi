package cn.cyc.ai.cog.runtime.planner;

import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.support.RuntimeContextParameters;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 计划驱动 Tool 选择器：按 taskPlan 或 preferredToolCode 从可用 Tool 列表中选择。
 *
 * @author cyc
 */
@Component
public class PlanDrivenToolSelector {

    /**
     * 是否启用计划驱动 Tool 选择。
     *
     * @param context 运行时上下文
     * @return 是否启用
     */
    public boolean isEnabled(ExecutionContext context) {
        return RuntimeContextParameters.flag(context, "planDrivenToolEnabled")
                || RuntimeContextParameters.flag(context, "planningEnabled");
    }

    /**
     * 从可用 Tool 列表中选择本次执行的 Tool。
     *
     * @param availableToolCodes 可用 Tool 编码
     * @param taskPlan           任务规划
     * @param context            运行时上下文
     * @return 选中的 Tool 编码
     */
    public String selectTool(List<String> availableToolCodes, Optional<TaskPlan> taskPlan, ExecutionContext context) {
        if (availableToolCodes == null || availableToolCodes.isEmpty()) {
            throw new IllegalArgumentException("availableToolCodes 不能为空");
        }
        Optional<String> preferredToolCode = RuntimeContextParameters.stringValue(context, "preferredToolCode")
                .filter(availableToolCodes::contains);
        if (preferredToolCode.isPresent()) {
            return preferredToolCode.get();
        }
        if (isEnabled(context) && taskPlan.isPresent()) {
            Optional<String> plannedTool = selectFromPlan(taskPlan.get(), availableToolCodes);
            if (plannedTool.isPresent()) {
                return plannedTool.get();
            }
        }
        return availableToolCodes.get(0);
    }

    private Optional<String> selectFromPlan(TaskPlan plan, List<String> availableToolCodes) {
        for (TaskPlanStep step : plan.steps()) {
            if (StringUtils.hasText(step.toolCode()) && availableToolCodes.contains(step.toolCode())) {
                return Optional.of(step.toolCode());
            }
            for (String toolCode : availableToolCodes) {
                if (containsIgnoreCase(step.action(), toolCode) || containsIgnoreCase(step.description(), toolCode)) {
                    return Optional.of(toolCode);
                }
            }
        }
        return Optional.empty();
    }

    private boolean containsIgnoreCase(String source, String target) {
        if (!StringUtils.hasText(source) || !StringUtils.hasText(target)) {
            return false;
        }
        return source.toLowerCase(Locale.ROOT).contains(target.toLowerCase(Locale.ROOT));
    }
}
