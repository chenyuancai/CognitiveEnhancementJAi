package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 学习辅导蓝图：描述本轮教学目标、步骤与下一步动作。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringBlueprint {

    /** 会话 ID。 */
    private String sessionId;

    /** 链路 Trace ID。 */
    private String traceId;

    /** 学习意图。 */
    private String intent;

    /** 当前选用的教学策略。 */
    private String selectedStrategy;

    /** 策略选择原因。 */
    private String strategyReason;

    /** 本轮学习目标。 */
    private String learningGoal;

    /** 推断出的学生状态。 */
    private AppTutoringStudentState studentState = new AppTutoringStudentState();

    /** 本轮实际使用的上下文。 */
    private AppTutoringContextUsed contextUsed = new AppTutoringContextUsed();

    /** 教学步骤计划。 */
    private List<AppTutoringBlueprintStep> teachingPlan = new ArrayList<>();

    /** 下一步推荐动作。 */
    private AppTutoringNextAction nextAction = new AppTutoringNextAction();

    /** 策略降级时的兜底策略。 */
    private String fallbackStrategy;
}
