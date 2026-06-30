package cn.cyc.ai.cog.app.insights.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 学习画像概览 BFF 响应（聚合练习、辅导画像、复习）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppInsightsOverviewVO {

    /** 概览统计卡片 */
    private List<Map<String, Object>> overviewStats;

    /** 练习类型分布 */
    private Map<String, Object> typeDistribution;

    /** 标签掌握度 */
    private List<Map<String, Object>> tagMastery;

    /** 薄弱知识点 */
    private List<Map<String, Object>> weakPoints;

    /** 学习热力图（首期占位） */
    private Map<String, Object> heatmap;

    /** 正确率趋势折线点 */
    private List<Map<String, Object>> accuracyTrendPoints;

    /** 趋势高亮 X 坐标 */
    private List<Integer> accuracyTrendHighlights;

    /** 最近练习会话 */
    private List<Map<String, Object>> recentSessions;
}
