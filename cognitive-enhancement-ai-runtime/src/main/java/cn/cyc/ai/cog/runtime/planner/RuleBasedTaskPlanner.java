package cn.cyc.ai.cog.runtime.planner;

import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.support.RuntimeContextParameters;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 规则型任务规划器：基于用户输入生成固定三步规划。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class RuleBasedTaskPlanner implements TaskPlanner {

    /**
     * 执行计划。
     *
     * @param context 上下文
     * @return 执行结果
     */
    @Override
    public Optional<TaskPlan> plan(ExecutionContext context) {
        if (!RuntimeContextParameters.flag(context, "planningEnabled")) {
            return Optional.empty();
        }
        String goal = resolveGoal(context);
        return Optional.of(new TaskPlan(
                goal,
                List.of(
                        new TaskPlanStep(1, "ANALYZE", "分析用户意图与约束", "PLANNED"),
                        new TaskPlanStep(2, "TOOL", "调用 tool.search 获取上下文", "PLANNED", "tool.search"),
                        new TaskPlanStep(3, "MERGE", "汇总输出", "PLANNED")
                )
        ));
    }

    /**
     * 执行resolveGoal。
     *
     * @param context 上下文
     * @return 执行结果
     */
    private String resolveGoal(ExecutionContext context) {
        Map<String, Object> input = context.request().input();
        Object question = input.get("question");
        if (question != null) {
            return String.valueOf(question);
        }
        Object query = input.get("query");
        if (query != null) {
            return String.valueOf(query);
        }
        return context.capability().capabilityCode();
    }
}
