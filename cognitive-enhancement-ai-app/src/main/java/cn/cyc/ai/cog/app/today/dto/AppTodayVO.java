package cn.cyc.ai.cog.app.today.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 今日页 BFF 聚合响应（对齐 today 契约）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppTodayVO {

    /** 欢迎区 */
    private Map<String, Object> welcome;

    /** 统计卡片 */
    private List<Map<String, Object>> statCards;

    /** 继续学习卡片 */
    private Map<String, Object> resumeLearning;

    /** 复习区摘要 */
    private Map<String, Object> reviewSection;

    /** 推荐卡片 */
    private List<Map<String, Object>> recommendations;

    /** 导入任务状态 */
    private Map<String, Object> importStatus;

    /** Banner 列表 */
    private List<?> banners;

    /** 公告列表 */
    private List<?> announcements;
}
