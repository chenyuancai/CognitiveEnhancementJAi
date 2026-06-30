package cn.cyc.ai.cog.runtime.planner;

import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 组合任务规划器：按 planningMode 在规则规划与 LLM 规划间路由。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Primary
@Component
public class CompositeTaskPlanner implements TaskPlanner {

    /** properties。 */
    private final PlanningProperties properties;
    /** ruleBasedTaskPlanner。 */
    private final RuleBasedTaskPlanner ruleBasedTaskPlanner;
    /** llmTaskPlanner。 */
    private final LlmTaskPlanner llmTaskPlanner;

    /**
     * 创建CompositeTaskPlanner。
     */
    public CompositeTaskPlanner(PlanningProperties properties,
                                RuleBasedTaskPlanner ruleBasedTaskPlanner,
                                LlmTaskPlanner llmTaskPlanner) {
        this.properties = properties;
        this.ruleBasedTaskPlanner = ruleBasedTaskPlanner;
        this.llmTaskPlanner = llmTaskPlanner;
    }

    /**
     * 执行计划。
     *
     * @param context 上下文
     * @return 执行结果
     */
    @Override
    public Optional<TaskPlan> plan(ExecutionContext context) {
        if (!properties.isEnabled()) {
            return Optional.empty();
        }
        PlanningMode mode = PlanningMode.from(context.request().parameters().get("planningMode"));
        if (mode == PlanningMode.LLM && properties.isLlmEnabled()) {
            return llmTaskPlanner.plan(context);
        }
        return ruleBasedTaskPlanner.plan(context);
    }
}
