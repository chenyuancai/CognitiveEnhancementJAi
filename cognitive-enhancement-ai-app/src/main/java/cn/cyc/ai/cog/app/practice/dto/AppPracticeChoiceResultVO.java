package cn.cyc.ai.cog.app.practice.dto;

import lombok.Data;

/**
 * 选择题作答结果 VO。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPracticeChoiceResultVO {

    /** 会话 ID */
    private String sessionId;

    /** 是否答对 */
    private Boolean correct;

    /** 得分 */
    private Integer score;

    /** 正确答案选项键 */
    private String correctAnswer;

    /** 解析说明 */
    private String explanation;

    /** 作答记录 ID */
    private Long answerId;
}
