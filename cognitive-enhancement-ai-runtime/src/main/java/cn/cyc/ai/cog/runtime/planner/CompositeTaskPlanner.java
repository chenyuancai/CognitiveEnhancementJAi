package cn.cyc.ai.cog.runtime.planner;

import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 组合任务规划器：按 planningMode 在规则规划与 LLM 规划间路由。
 *
 * @author cyc
 */
@Primary
@Component
public class CompositeTaskPlanner implements TaskPlanner {

    private final PlanningProperties properties;
    private final RuleBasedTaskPlanner ruleBasedTaskPlanner;
    private final LlmTaskPlanner llmTaskPlanner;

    public CompositeTaskPlanner(PlanningProperties properties,
                                RuleBasedTaskPlanner ruleBasedTaskPlanner,
                                LlmTaskPlanner llmTaskPlanner) {
        this.properties = properties;
        this.ruleBasedTaskPlanner = ruleBasedTaskPlanner;
        this.llmTaskPlanner = llmTaskPlanner;
    }

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
