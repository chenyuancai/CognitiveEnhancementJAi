package cn.cyc.ai.cog.app.tutoring.strategy;

import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringLlmAnalysisResult;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * C 端学习辅导教学策略决策器：根据用户输入、会话状态与画像选择意图与教学动作。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringStrategyDecider {

    /**
     * 基于单条消息进行策略决策（已废弃，请使用 {@link #decide(AppTutoringStrategyInput)}）。
     *
     * @param message 用户消息
     * @return 策略决策结果
     * @deprecated 请使用包含完整上下文的 {@link #decide(AppTutoringStrategyInput)}
     */
    /**
     * 执行decide。
     *
     * @param message 消息
     * @return 执行结果
     */
    @Deprecated
    public AppTutoringStrategyDecision decide(String message) {
        AppTutoringStudentState state = new AppTutoringStudentState();
        state.setStuckCount(0);
        return decide(AppTutoringStrategyInput.basic(message, List.of(), state, null, 2));
    }

    /**
     * 根据完整决策输入选择学习意图与教学策略。
     *
     * @param input 策略决策输入
     * @return 策略决策结果
     */
    public AppTutoringStrategyDecision decide(AppTutoringStrategyInput input) {
        String message = input.message();
        String normalized = StringUtils.hasText(message) ? message.trim().toLowerCase(Locale.ROOT) : "";
        int stuckCount = input.studentState() == null ? 0 : input.studentState().getStuckCount();

        if (stuckCount >= input.stuckFallbackThreshold()) {
            return decision(AppTutoringIntent.CONCEPT_EXPLANATION, AppTeachingStrategy.STEP_BY_STEP_EXPLANATION,
                    "学生已连续卡住，切换为分步讲解。", "STEP_EXPLANATION", false);
        }
        if (profileNeedsRemedial(input.profile(), input.studentState())) {
            return decision(AppTutoringIntent.CONCEPT_EXPLANATION, AppTeachingStrategy.REMEDIAL_TEACHING,
                    "画像显示该知识点需要补学。", "REMEDIAL_TEACHING", false);
        }
        if (containsAny(normalized, "没基础", "零基础", "完全不会", "前置", "底子薄")) {
            return decision(AppTutoringIntent.CONCEPT_EXPLANATION, AppTeachingStrategy.REMEDIAL_TEACHING,
                    "用户基础薄弱，优先补充前置知识。", "REMEDIAL_TEACHING", false);
        }
        if (containsAny(normalized, "直接", "答案", "是什么", "定义")) {
            return decision(AppTutoringIntent.FACT_QA, AppTeachingStrategy.DIRECT_ANSWER,
                    "用户表达了直接获取答案或定义的诉求，优先直接回答并保留简短引导。",
                    "FINAL_ANSWER", false);
        }
        if (containsAny(normalized, "总结", "概括", "复习")
                || hasReferenceMaterial(input) && containsAny(normalized, "资料", "文档", "文件", "内容")) {
            return decision(AppTutoringIntent.SUMMARY_REVIEW, AppTeachingStrategy.SUMMARY_REVIEW,
                    "用户要求总结或复习资料，不强行追问。", "SUMMARY_REVIEW", false);
        }
        if (containsAny(normalized, "计划", "规划", "怎么学", "学习路线")) {
            return decision(AppTutoringIntent.LEARNING_PLAN, AppTeachingStrategy.LEARNING_PLAN,
                    "用户关注长期提升目标，生成学习规划更合适。", "LEARNING_PLAN", false);
        }
        if (isLlmIntent(input.llmAnalysis(), "MISTAKE_ANALYSIS")
                || containsAny(normalized, "错", "错题", "哪里不对", "为什么错")) {
            return decision(AppTutoringIntent.MISTAKE_ANALYSIS, AppTeachingStrategy.HINT_THEN_QUESTION,
                    "错题分析适合先定位思路偏差，再引导学生修正。", "ASK_GUIDING_QUESTION", true);
        }
        if (isReasoning(input.llmAnalysis(), AppReasoningJudgment.INCORRECT)) {
            return decision(AppTutoringIntent.CONCEPT_EXPLANATION, AppTeachingStrategy.HINT_THEN_QUESTION,
                    "学生思路存在偏差，先纠偏再追问。", "ASK_GUIDING_QUESTION", true);
        }
        if (isReasoning(input.llmAnalysis(), AppReasoningJudgment.CORRECT)
                || masteryPracticeReady(input.studentState())
                || containsAny(normalized, "我明白了", "懂了", "知道了", "原来如此")) {
            return decision(AppTutoringIntent.CONCEPT_EXPLANATION, AppTeachingStrategy.PRACTICE_CHECK,
                    "学生已表达理解，适合用小练习巩固。", "PRACTICE_CHECK", true);
        }
        if (containsAny(normalized, "练习", "小测", "测试一下", "做题")) {
            return decision(AppTutoringIntent.CONCEPT_EXPLANATION, AppTeachingStrategy.PRACTICE_CHECK,
                    "用户希望检验掌握程度，适合用小练习检查。", "PRACTICE_CHECK", true);
        }
        if (containsAny(normalized, "怎么做", "解题", "题", "证明", "计算")) {
            return decision(AppTutoringIntent.PROBLEM_SOLVING, AppTeachingStrategy.HINT_THEN_QUESTION,
                    "解题类问题适合先提示关键条件，再让学生说出下一步。", "ASK_GUIDING_QUESTION", true);
        }
        return decision(AppTutoringIntent.CONCEPT_EXPLANATION, AppTeachingStrategy.HINT_THEN_QUESTION,
                "概念理解类问题默认采用先提示再追问，避免直接代替学生思考。",
                "ASK_GUIDING_QUESTION", true);
    }

    /**
     * 组装策略决策结果。
     *
     * @param intent         学习意图
     * @param strategy       教学策略
     * @param reason         策略选择原因
     * @param nextAction     下一步动作类型
     * @param needUserReply  是否期待用户回复
     * @return 策略决策结果
     */
    private AppTutoringStrategyDecision decision(AppTutoringIntent intent,
                                               AppTeachingStrategy strategy,
                                               String reason,
                                               String nextAction,
                                               boolean needUserReply) {
        return new AppTutoringStrategyDecision(intent, strategy, reason, nextAction, needUserReply);
    }

    /**
     * 判断画像是否显示当前知识点需要补学。
     *
     * @param profile 用户学习画像
     * @param state   学生学习状态
     * @return 是否需要补学
     */
    private boolean profileNeedsRemedial(AppLearningProfile profile, AppTutoringStudentState state) {
        if (profile == null || state == null || !StringUtils.hasText(state.getKnowledgePoint())) {
            return false;
        }
        return profile.getKnowledgePoints().stream()
                .anyMatch(point -> state.getKnowledgePoint().equals(point.getName())
                        && point.getMastery() == AppMasteryLevel.NEEDS_REMEDIAL);
    }

    /**
     * 判断学生掌握度是否已达到可练习状态。
     *
     * @param state 学生学习状态
     * @return 是否可进入练习
     */
    private boolean masteryPracticeReady(AppTutoringStudentState state) {
        return state != null && AppMasteryLevel.PRACTICE_READY.name().equals(state.getMasteryLevel());
    }

    /**
     * 判断 LLM 分析意图是否匹配指定值。
     *
     * @param analysis LLM 分析结果
     * @param intent   目标意图
     * @return 是否匹配
     */
    private boolean isLlmIntent(AppTutoringLlmAnalysisResult analysis, String intent) {
        return analysis != null && intent.equalsIgnoreCase(analysis.intent());
    }

    /**
     * 判断 LLM 推理判断是否与指定值一致。
     *
     * @param analysis  LLM 分析结果
     * @param judgment  目标推理判断
     * @return 是否一致
     */
    private boolean isReasoning(AppTutoringLlmAnalysisResult analysis, AppReasoningJudgment judgment) {
        return analysis != null && judgment.name().equalsIgnoreCase(analysis.reasoningJudgment());
    }

    /**
     * 判断决策输入是否包含知识或文件引用资料。
     *
     * @param input 策略决策输入
     * @return 是否包含引用资料
     */
    private boolean hasReferenceMaterial(AppTutoringStrategyInput input) {
        if (input.resolvedContext() == null) {
            return false;
        }
        return !CollectionUtils.isEmpty(input.resolvedContext().getKnowledgeSnippets())
                || !CollectionUtils.isEmpty(input.resolvedContext().getFileSnippets());
    }

    /**
     * 判断文本是否包含任一关键词。
     *
     * @param value    待检测文本
     * @param keywords 关键词列表
     * @return 是否包含
     */
    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
