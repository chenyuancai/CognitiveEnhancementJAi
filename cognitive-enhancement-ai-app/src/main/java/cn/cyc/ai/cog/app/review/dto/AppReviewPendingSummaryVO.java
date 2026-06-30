package cn.cyc.ai.cog.app.review.dto;

import lombok.Data;

import java.util.List;

/**
 * 待复习汇总（今日页 reviewSection 使用）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppReviewPendingSummaryVO {

    /** 待复习总数 */
    private long total;

    /** 逾期条数 */
    private long overdueCount;

    /** 预览条目（最多 N 条） */
    private List<AppReviewPendingItemVO> items;
}
