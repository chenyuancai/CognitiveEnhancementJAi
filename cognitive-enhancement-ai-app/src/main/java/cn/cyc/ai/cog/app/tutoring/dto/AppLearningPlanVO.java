package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * C 端学习计划视图。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppLearningPlanVO {

    /** 计划 ID。 */
    private Long id;

    /** 计划标题。 */
    private String planTitle;

    /** 计划原始 JSON（与 stages 二选一或并存）。 */
    private String planJson;

    /** 结构化阶段列表。 */
    private List<PlanStage> stages = new ArrayList<>();

    /** 计划状态。 */
    private String status;

    /** 关联会话 ID。 */
    private String sessionId;

    /** 链路 Trace ID。 */
    private String traceId;

    /**
     * 学习计划阶段。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class PlanStage {

        /** 阶段序号。 */
        private int order;

        /** 阶段标题。 */
        private String title;

        /** 阶段目标描述。 */
        private String goal;
    }
}
