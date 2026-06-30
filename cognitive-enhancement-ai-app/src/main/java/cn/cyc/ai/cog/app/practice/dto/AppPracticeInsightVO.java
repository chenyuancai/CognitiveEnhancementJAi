package cn.cyc.ai.cog.app.practice.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 练习洞察 VO（今日统计、待复习紧迫度、进行中会话）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPracticeInsightVO {

    /** 今日统计卡片 */
    private List<Map<String, Object>> todayStats;

    /** 复习紧迫度摘要 */
    private Map<String, Object> reviewUrgency;

    /** 进行中会话摘要 */
    private Map<String, Object> session;
}
