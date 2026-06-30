package cn.cyc.ai.cog.app.support;

import org.springframework.util.StringUtils;

/**
 * 运营域展示标签辅助（Banner 行为、公告权重、站内信分类、工单状态等）。
 *
 * @author cyc
 * @date 2026/6/29
 */
public final class AppOpsLabelSupport {

    private AppOpsLabelSupport() {
    }

    /**
     * Banner 点击行为类型：有链接为 link，否则 none。
     */
    public static String bannerActionType(String linkUrl) {
        return StringUtils.hasText(linkUrl) ? "link" : "none";
    }

    /**
     * Banner 跳转地址（契约 actionUrl）。
     */
    public static String bannerActionUrl(String linkUrl) {
        return linkUrl;
    }

    /**
     * 公告排序权重（首期由发布时间派生占位值）。
     */
    public static int announcementPriority(java.time.LocalDateTime publishAt) {
        if (publishAt == null) {
            return 0;
        }
        return (int) Math.min(100, publishAt.toEpochSecond(java.time.ZoneOffset.UTC) % 100);
    }

    /**
     * 站内信模板编码 → 分类展示名。
     */
    public static String messageCategoryLabel(String templateCode) {
        if (!StringUtils.hasText(templateCode)) {
            return "系统通知";
        }
        return switch (templateCode) {
            case "BILLING" -> "账单通知";
            case "MEMBERSHIP" -> "会员通知";
            case "OPS" -> "运营通知";
            default -> "系统通知";
        };
    }

    /**
     * 工单状态编码 → 中文标签。
     */
    public static String ticketStatusLabel(String status) {
        if (!StringUtils.hasText(status)) {
            return "未知";
        }
        return switch (status.toUpperCase()) {
            case "OPEN" -> "待处理";
            case "IN_PROGRESS" -> "处理中";
            case "RESOLVED" -> "已解决";
            case "CLOSED" -> "已关闭";
            default -> status;
        };
    }
}
