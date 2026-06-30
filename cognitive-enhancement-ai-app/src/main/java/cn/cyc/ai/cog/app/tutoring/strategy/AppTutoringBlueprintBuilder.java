package cn.cyc.ai.cog.app.tutoring.strategy;

import cn.cyc.ai.cog.app.tutoring.context.AppTutoringLoadedContext;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringBlueprint;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringBlueprintStep;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatRequest;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringContextUsed;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringNextAction;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringReferences;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 学习辅导蓝图组装器：根据策略决策生成教学步骤与下一步动作。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringBlueprintBuilder {

    /**
     * 根据策略决策与上下文组装教学蓝图。
     *
     * @param request      聊天请求
     * @param sessionId    会话 ID
     * @param traceId      追踪 ID
     * @param decision     策略决策结果
     * @param studentState 学生学习状态
     * @param context      已加载上下文
     * @return 教学蓝图
     */
    public AppTutoringBlueprint build(AppTutoringChatRequest request,
                                      String sessionId,
                                      String traceId,
                                      AppTutoringStrategyDecision decision,
                                      AppTutoringStudentState studentState,
                                      AppTutoringLoadedContext context) {
        AppTutoringBlueprint blueprint = new AppTutoringBlueprint();
        blueprint.setSessionId(sessionId);
        blueprint.setTraceId(traceId);
        blueprint.setIntent(decision.intent().name());
        blueprint.setSelectedStrategy(decision.strategy().name());
        blueprint.setStrategyReason(decision.reason());
        blueprint.setLearningGoal(resolveLearningGoal(decision.intent()));
        blueprint.setFallbackStrategy(AppTeachingStrategy.STEP_BY_STEP_EXPLANATION.name());
        blueprint.setStudentState(copyStudentState(studentState, decision));
        blueprint.setContextUsed(buildContextUsed(request.getReferences(), context));
        blueprint.setTeachingPlan(teachingPlan(decision.strategy()));
        blueprint.setNextAction(nextAction(decision));
        return blueprint;
    }

    /**
     * 复制并补全学生学习状态快照。
     *
     * @param source   原始学生状态
     * @param decision 策略决策结果
     * @return 学生状态快照
     */
    private AppTutoringStudentState copyStudentState(AppTutoringStudentState source,
                                                     AppTutoringStrategyDecision decision) {
        AppTutoringStudentState studentState = new AppTutoringStudentState();
        if (source != null) {
            studentState.setKnowledgePoint(source.getKnowledgePoint());
            studentState.setMasteryLevel(source.getMasteryLevel());
            studentState.setConfusion(source.getConfusion());
            studentState.setStuckCount(source.getStuckCount());
        } else {
            studentState.setKnowledgePoint("UNKNOWN");
            studentState.setMasteryLevel("UNKNOWN");
            studentState.setConfusion(decision.intent() == AppTutoringIntent.FACT_QA ? "" : "待通过追问确认");
            studentState.setStuckCount(0);
        }
        return studentState;
    }

    /**
     * 构建本轮使用的上下文引用摘要。
     *
     * @param references 用户引用
     * @param context    已加载上下文
     * @return 上下文使用摘要
     */
    private AppTutoringContextUsed buildContextUsed(AppTutoringReferences references,
                                                    AppTutoringLoadedContext context) {
        AppTutoringContextUsed contextUsed = new AppTutoringContextUsed();
        contextUsed.setRecentMessages(!context.recentMessages().isEmpty());
        contextUsed.setSummary(StringUtils.hasText(context.sessionSummary()));
        if (references != null) {
            contextUsed.setKnowledgeRefs(references.getKnowledgeIds());
            contextUsed.setFileRefs(references.getFileIds());
            contextUsed.setMessageRefs(references.getMessageIds());
            contextUsed.setSelectedText(references.getSelectedText());
        }
        return contextUsed;
    }

    /**
     * 根据学习意图解析本轮学习目标描述。
     *
     * @param intent 学习意图
     * @return 学习目标描述
     */
    private String resolveLearningGoal(AppTutoringIntent intent) {
        return switch (intent) {
            case LEARNING_PLAN -> "形成可执行的阶段学习路径";
            case SUMMARY_REVIEW -> "提炼资料要点并形成复习线索";
            case FACT_QA -> "快速理解问题答案和关键依据";
            case MISTAKE_ANALYSIS -> "定位错误原因并修正解题思路";
            case PROBLEM_SOLVING -> "掌握解题突破口和下一步推理";
            case FREE_CHAT -> "维持自然学习交流";
            case CONCEPT_EXPLANATION -> "理解当前概念及其适用边界";
        };
    }

    /**
     * 根据教学策略生成教学步骤计划。
     *
     * @param strategy 教学策略
     * @return 教学步骤列表
     */
    private List<AppTutoringBlueprintStep> teachingPlan(AppTeachingStrategy strategy) {
        List<AppTutoringBlueprintStep> steps = new ArrayList<>();
        switch (strategy) {
            case DIRECT_ANSWER -> {
                steps.add(new AppTutoringBlueprintStep(1, "ANSWER", "直接给出答案和关键依据"));
                steps.add(new AppTutoringBlueprintStep(2, "THINKING_PROMPT", "补一句可继续思考的提示"));
            }
            case SUMMARY_REVIEW -> {
                steps.add(new AppTutoringBlueprintStep(1, "SUMMARY", "提炼资料核心要点"));
                steps.add(new AppTutoringBlueprintStep(2, "REVIEW_HINT", "给出复习关注点"));
            }
            case LEARNING_PLAN -> {
                steps.add(new AppTutoringBlueprintStep(1, "GOAL_BREAKDOWN", "拆解学习目标"));
                steps.add(new AppTutoringBlueprintStep(2, "PLAN", "生成阶段路径"));
            }
            case STEP_BY_STEP_EXPLANATION -> {
                steps.add(new AppTutoringBlueprintStep(1, "STEP_BREAKDOWN", "拆成可跟进的步骤"));
                steps.add(new AppTutoringBlueprintStep(2, "STEP_EXPLAIN", "逐步讲解每一步"));
                steps.add(new AppTutoringBlueprintStep(3, "CHECKPOINT", "确认学生是否跟上"));
            }
            case REMEDIAL_TEACHING -> {
                steps.add(new AppTutoringBlueprintStep(1, "PREREQUISITE", "补充前置概念"));
                steps.add(new AppTutoringBlueprintStep(2, "SIMPLIFY", "用更简单的例子讲解"));
                steps.add(new AppTutoringBlueprintStep(3, "BRIDGE", "桥接回当前问题"));
            }
            case PRACTICE_CHECK -> {
                steps.add(new AppTutoringBlueprintStep(1, "MINI_PRACTICE", "给出小练习"));
                steps.add(new AppTutoringBlueprintStep(2, "FEEDBACK", "根据回答给反馈"));
            }
            default -> {
                steps.add(new AppTutoringBlueprintStep(1, "HINT", "先给一个轻提示"));
                steps.add(new AppTutoringBlueprintStep(2, "QUESTION", "引导学生说出自己的理解"));
                steps.add(new AppTutoringBlueprintStep(3, "EXPLANATION", "学生卡住时再分步讲解"));
                steps.add(new AppTutoringBlueprintStep(4, "PRACTICE_CHECK", "用小练习检查掌握"));
            }
        }
        return steps;
    }

    /**
     * 根据策略决策构建下一步动作指引。
     *
     * @param decision 策略决策结果
     * @return 下一步动作
     */
    private AppTutoringNextAction nextAction(AppTutoringStrategyDecision decision) {
        AppTutoringNextAction nextAction = new AppTutoringNextAction();
        nextAction.setType(decision.nextActionType());
        nextAction.setContent(nextActionContent(decision.nextActionType()));
        return nextAction;
    }

    /**
     * 根据动作类型生成面向模型的动作内容描述。
     *
     * @param nextActionType 下一步动作类型
     * @return 动作内容描述
     */
    private String nextActionContent(String nextActionType) {
        if ("FINAL_ANSWER".equals(nextActionType)) {
            return "直接回答，并保留一个简短思考提示。";
        }
        if ("SUMMARY_REVIEW".equals(nextActionType)) {
            return "总结资料要点，并指出复习重点。";
        }
        if ("LEARNING_PLAN".equals(nextActionType)) {
            return "拆解学习目标并给出阶段计划。";
        }
        if ("STEP_EXPLANATION".equals(nextActionType)) {
            return "分步讲解，每步确认学生是否理解。";
        }
        if ("REMEDIAL_TEACHING".equals(nextActionType)) {
            return "先补前置知识，再回到当前问题。";
        }
        if ("PRACTICE_CHECK".equals(nextActionType)) {
            return "给出小练习并等待学生作答。";
        }
        return "先给轻提示，再问一个具体问题引导学生继续思考。";
    }
}
