package cn.cyc.ai.cog.app.tutoring.dto;

import cn.cyc.ai.cog.app.tutoring.strategy.AppMasteryLevel;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTeachingStrategy;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * C 端用户学习画像快照。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppLearningProfile {

    /** 用户 ID。 */
    private Long userId;

    /** 整体掌握程度。 */
    private AppMasteryLevel overallMastery = AppMasteryLevel.UNKNOWN;

    /** 各知识点掌握情况。 */
    private List<KnowledgePointMastery> knowledgePoints = new ArrayList<>();

    /** 薄弱主题列表。 */
    private List<String> weakTopics = new ArrayList<>();

    /** 当前活跃学习计划 ID。 */
    private Long activePlanId;

    /** 画像最后更新时间。 */
    private Instant lastUpdatedAt;

    /**
     * 单个知识点的掌握情况。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class KnowledgePointMastery {

        /** 知识点名称。 */
        private String name;

        /** 掌握程度。 */
        private AppMasteryLevel mastery = AppMasteryLevel.UNKNOWN;

        /** 连续卡住次数。 */
        private int stuckCount;

        /** 上次使用的教学策略。 */
        private AppTeachingStrategy lastStrategy;
    }
}
