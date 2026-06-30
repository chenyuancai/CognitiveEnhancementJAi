package cn.cyc.ai.cog.app.practice.dto;

import lombok.Data;

/**
 * 选择题作答请求。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPracticeChoiceAnswerRequest {

    /** 题目 ID */
    private String questionId;

    /** 选项键（如 A/B/C/D） */
    private String answerKey;
}
