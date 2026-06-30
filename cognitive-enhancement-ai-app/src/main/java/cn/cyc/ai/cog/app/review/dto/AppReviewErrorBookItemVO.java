package cn.cyc.ai.cog.app.review.dto;

import lombok.Data;

/**
 * 错题本列表项 VO。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppReviewErrorBookItemVO {

    /** 错题 ID */
    private String id;

    /** 标题/知识点 */
    private String title;

    /** 关联内容 ID */
    private Long contentId;

    /** 得分 */
    private Integer score;

    /** 标签 */
    private String tag;
}
