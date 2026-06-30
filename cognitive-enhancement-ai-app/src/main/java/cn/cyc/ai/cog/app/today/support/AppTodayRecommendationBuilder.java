package cn.cyc.ai.cog.app.today.support;

import cn.cyc.ai.cog.app.review.dto.AppReviewPendingItemVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 今日页推荐卡片构建器（首期基于待复习条目生成）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Component
public class AppTodayRecommendationBuilder {

    /**
     * 将待复习条目转为推荐卡片列表。
     *
     * @param items 待复习项
     * @param limit 最多条数
     * @return recommendations 区块
     */
    public List<Map<String, Object>> buildFromPendingItems(List<AppReviewPendingItemVO> items, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        items.stream().limit(limit).forEach(item -> recommendations.add(Map.of(
                "tone", "rose",
                "type", "review",
                "title", item.getTitle(),
                "desc", "待复习条目",
                "action", "开始复习",
                "itemId", item.getId())));
        return recommendations;
    }
}
