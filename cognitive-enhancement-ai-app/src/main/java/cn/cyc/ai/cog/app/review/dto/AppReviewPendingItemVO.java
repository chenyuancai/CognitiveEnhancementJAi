package cn.cyc.ai.cog.app.review.dto;

import lombok.Data;

/**
 * 待复习列表项 VO。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppReviewPendingItemVO {

    /** 条目 ID */
    private String id;

    /** 标题 */
    private String title;

    /** 紧迫度：OVERDUE / NORMAL 等 */
    private String urgency;

    /** 到期说明文案 */
    private String dueText;

    /** 知识标签 */
    private String tag;

    /** 最近正确率/得分 */
    private Integer accuracy;

    /** 预计复习时长 */
    private String duration;
}
