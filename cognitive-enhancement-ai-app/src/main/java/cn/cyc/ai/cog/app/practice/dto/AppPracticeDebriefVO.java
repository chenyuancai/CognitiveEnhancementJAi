package cn.cyc.ai.cog.app.practice.dto;

import lombok.Data;

import java.util.Map;

/**
 * 练习复盘 VO。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPracticeDebriefVO {

    /** 会话 ID */
    private String sessionId;

    /** 练习标题 */
    private String title;

    /** 作答统计：total / correct / accuracy */
    private Map<String, Object> answerSummary;
}
