package cn.cyc.ai.cog.app.practice.dto;

import lombok.Data;

/**
 * 问答题作答请求。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPracticeEssayAnswerRequest {

    /** 题目 ID */
    private String questionId;

    /** 用户作答正文 */
    private String answer;
}
