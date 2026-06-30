package cn.cyc.ai.cog.app.practice.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建练习会话响应 VO（对齐 practice.md）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPracticeSessionVO {

    /** 会话 ID（对外 sessionCode） */
    private String sessionId;

    /** 题目总数 */
    private Integer questionCount;

    /** 题目队列 */
    private List<AppPracticeQuestionVO> questions;
}
