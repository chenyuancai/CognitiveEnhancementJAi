package cn.cyc.ai.cog.app.insights.assembler;

import cn.cyc.ai.cog.app.review.dto.AppReviewRecentSessionVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学习画像 BFF 图表数据组装器。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Component
public class AppInsightsAssembler {

    /**
     * 根据最近练习会话构建正确率趋势折线点。
     */
    public List<Map<String, Object>> buildAccuracyTrend(List<AppReviewRecentSessionVO> sessions) {
        List<Map<String, Object>> points = new ArrayList<>();
        int x = 50;
        for (AppReviewRecentSessionVO session : sessions) {
            Map<String, Object> point = new HashMap<>();
            point.put("x", x);
            point.put("y", session.getAccuracy() == null ? 0 : session.getAccuracy());
            points.add(point);
            x += 40;
        }
        return points;
    }

    /**
     * 趋势图高亮锚点（首期取首尾会话）。
     */
    public List<Integer> buildHighlights(List<AppReviewRecentSessionVO> sessions) {
        List<Integer> highlights = new ArrayList<>();
        int x = 50;
        for (int i = 0; i < sessions.size(); i++) {
            if (i == 0 || i == sessions.size() - 1) {
                highlights.add(x);
            }
            x += 40;
        }
        return highlights;
    }
}
