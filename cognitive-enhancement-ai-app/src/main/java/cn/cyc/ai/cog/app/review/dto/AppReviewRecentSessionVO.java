package cn.cyc.ai.cog.app.review.dto;

import lombok.Data;

/**
 * 最近练习会话 VO。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppReviewRecentSessionVO {

    /** 会话 ID */
    private String id;

    /** 标题 */
    private String title;

    /** 正确率（0-100） */
    private Integer accuracy;

    /** 作答题数 */
    private Integer questions;

    /** 用时（分钟） */
    private Integer minutes;

    /** 练习模式 */
    private String mode;
}
