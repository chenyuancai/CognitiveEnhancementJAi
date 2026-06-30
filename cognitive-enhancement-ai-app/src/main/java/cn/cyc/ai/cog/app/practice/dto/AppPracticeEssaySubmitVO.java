package cn.cyc.ai.cog.app.practice.dto;

import lombok.Data;

/**
 * 问答题提交响应（进入 AI 评分队列）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPracticeEssaySubmitVO {

    /** 会话 ID */
    private String sessionId;

    /** 作答记录 ID */
    private Long answerId;

    /** 评分任务 ID（用于 SSE 轮询） */
    private String jobId;
}
