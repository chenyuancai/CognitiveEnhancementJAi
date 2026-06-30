package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

/**
 * C 端练习推荐视图。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppPracticeRecommendationVO {

    /** 推荐记录 ID。 */
    private Long id;

    /** 关联会话 ID。 */
    private String sessionId;

    /** 关联知识点。 */
    private String knowledgePoint;

    /** 练习题目文本。 */
    private String promptText;

    /** 难度等级。 */
    private String difficulty;

    /** 推荐状态。 */
    private String status;
}
