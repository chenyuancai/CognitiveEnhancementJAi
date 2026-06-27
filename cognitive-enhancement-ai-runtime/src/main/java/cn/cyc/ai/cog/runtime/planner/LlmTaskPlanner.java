package cn.cyc.ai.cog.runtime.planner;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.api.ModelGovernanceResolution;
import cn.cyc.ai.cog.runtime.coordinator.ExecutionStrategy;
import cn.cyc.ai.cog.runtime.coordinator.MultiAgentCoordinator;
import cn.cyc.ai.cog.runtime.model.governance.DefaultModelGovernance;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import cn.cyc.ai.cog.runtime.support.RuntimeContextParameters;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * LLM 驱动任务规划器：通过模型生成分步计划。
 *
 * @author cyc
 */
@Component
public class LlmTaskPlanner implements TaskPlanner {

    private static final Logger log = LoggerFactory.getLogger(LlmTaskPlanner.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final PlanningProperties properties;
    private final DefaultModelGovernance modelGovernance;
    private final LlmGateway llmGateway;
    private final ObjectMapper objectMapper;
    private final TraceSpanRecorder traceSpanRecorder;
    private final RuleBasedTaskPlanner fallbackPlanner;

    public LlmTaskPlanner(PlanningProperties properties,
                          DefaultModelGovernance modelGovernance,
                          LlmGateway llmGateway,
                          ObjectMapper objectMapper,
                          TraceSpanRecorder traceSpanRecorder,
                          RuleBasedTaskPlanner fallbackPlanner) {
        this.properties = properties;
        this.modelGovernance = modelGovernance;
        this.llmGateway = llmGateway;
        this.objectMapper = objectMapper;
        this.traceSpanRecorder = traceSpanRecorder;
        this.fallbackPlanner = fallbackPlanner;
    }

    @Override
    public Optional<TaskPlan> plan(ExecutionContext context) {
        if (!RuntimeContextParameters.flag(context, "planningEnabled")) {
            return Optional.empty();
        }
        if (context.agent() == null) {
            return fallbackPlanner.plan(context);
        }
        String goal = resolveGoal(context);
        ExecutionStrategy strategy = MultiAgentCoordinator.resolveStrategy(context);
        ModelGovernanceResolution modelResolution = modelGovernance.resolveModel(context.agent().modelCode(), strategy);
        ModelDefinition model = modelResolution.resolvedModel();
        List<String> toolCodes = context.skills().stream()
                .flatMap(skill -> skill.boundToolCodes().stream())
                .distinct()
                .toList();

        TraceSpanRecorder.SpanScope planSpan = traceSpanRecorder.open(
                context.traceId(),
                TraceSpanType.PLAN,
                "llm-planner",
                Map.of("modelCode", model.modelCode(), "planningMode", "LLM"));
        try {
            Map<String, Object> plannerPrompt = buildPlannerPrompt(goal, toolCodes, context.skills());
            LlmInvocationResult llmResult = llmGateway.generate(context, model, plannerPrompt);
            Optional<TaskPlan> parsed = parsePlan(goal, llmResult.answer());
            if (parsed.isPresent()) {
                traceSpanRecorder.succeed(planSpan, Map.of("stepCount", parsed.get().steps().size()));
                return parsed;
            }
            log.warn("LLM 规划解析失败，回退规则规划, traceId={}", context.traceId());
            traceSpanRecorder.succeed(planSpan, Map.of("fallback", true));
            return fallbackPlanner.plan(context);
        } catch (RuntimeException ex) {
            traceSpanRecorder.fail(planSpan, ex, Map.of("failureReason", ex.getMessage()));
            log.warn("LLM 规划失败，回退规则规划, traceId={}, reason={}", context.traceId(), ex.getMessage());
            return fallbackPlanner.plan(context);
        }
    }

    private Map<String, Object> buildPlannerPrompt(String goal, List<String> toolCodes, List<SkillDefinition> skills) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("instruction", """
                你是任务规划器。根据用户目标输出 JSON，不要包含其它文字。
                格式: {"steps":[{"order":1,"action":"ACTION","description":"步骤描述"}]}
                至少 2 步，最多 6 步。""");
        payload.put("goal", goal);
        payload.put("availableToolCodes", toolCodes);
        payload.put("availableSkillCodes", skills.stream().map(SkillDefinition::skillCode).toList());
        return payload;
    }

    @SuppressWarnings("unchecked")
    Optional<TaskPlan> parsePlan(String goal, String answer) {
        if (answer == null || answer.isBlank()) {
            return Optional.empty();
        }
        try {
            String json = extractJson(answer);
            Map<String, Object> parsed = objectMapper.readValue(json, MAP_TYPE);
            Object stepsRaw = parsed.get("steps");
            if (!(stepsRaw instanceof List<?> stepsList) || stepsList.isEmpty()) {
                return Optional.empty();
            }
            List<TaskPlanStep> steps = new ArrayList<>();
            for (Object item : stepsList) {
                if (!(item instanceof Map<?, ?> stepMap)) {
                    continue;
                }
                int order = toInt(stepMap.get("order"), steps.size() + 1);
                String action = toText(stepMap.get("action"), "STEP");
                String description = toText(stepMap.get("description"), action);
                String toolCode = toText(stepMap.get("toolCode"), null);
                if (toolCode == null) {
                    steps.add(new TaskPlanStep(order, action, description, "PLANNED"));
                } else {
                    steps.add(new TaskPlanStep(order, action, description, "PLANNED", toolCode));
                }
            }
            if (steps.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new TaskPlan(goal, List.copyOf(steps)));
        } catch (Exception ex) {
            log.debug("解析 LLM 规划 JSON 失败: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private String extractJson(String answer) {
        int start = answer.indexOf('{');
        int end = answer.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return answer.substring(start, end + 1);
        }
        return answer.trim();
    }

    private int toInt(Object value, int defaultValue) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            return Integer.parseInt(text.trim());
        }
        return defaultValue;
    }

    private String toText(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        String text = String.valueOf(value);
        if (text.isBlank()) {
            return defaultValue;
        }
        if ("null".equalsIgnoreCase(text)) {
            return defaultValue;
        }
        return text;
    }

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
