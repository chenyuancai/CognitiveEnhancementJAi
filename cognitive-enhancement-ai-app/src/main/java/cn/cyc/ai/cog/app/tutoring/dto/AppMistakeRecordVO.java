package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * C 端错题记录视图。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppMistakeRecordVO {

    /** 错题记录 ID。 */
    private Long id;

    /** 关联会话 ID。 */
    private String sessionId;

    /** 链路 Trace ID。 */
    private String traceId;

    /** 关联知识点。 */
    private String knowledgePoint;

    /** 错误摘要。 */
    private String mistakeSummary;

    /** 学生作答思路。 */
    private String userApproach;

    /** 纠正提示。 */
    private String correctionHint;

    /** 关联内容 ID。 */
    private Long contentId;

    /** 得分。 */
    private Integer score;

    /** 标签。 */
    private String tag;

    /** 记录状态。 */
    private String status;

    /** 创建时间。 */
    private LocalDateTime createTime;
}
