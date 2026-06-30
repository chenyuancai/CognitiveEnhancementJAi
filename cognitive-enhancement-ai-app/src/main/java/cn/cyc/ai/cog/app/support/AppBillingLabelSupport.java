package cn.cyc.ai.cog.app.support;

import org.springframework.util.StringUtils;

/**
 * 计费域展示标签辅助（渠道名、套餐角标、额度预警等）。
 *
 * @author cyc
 * @date 2026/6/29
 */
public final class AppBillingLabelSupport {

    private AppBillingLabelSupport() {
    }

    /**
     * 支付渠道编码 → 中文展示名。
     */
    public static String payChannelLabel(String payChannel) {
        if (!StringUtils.hasText(payChannel)) {
            return "未知渠道";
        }
        return switch (payChannel.toUpperCase()) {
            case "WECHAT", "WECHAT_PAY" -> "微信支付";
            case "ALIPAY" -> "支付宝";
            case "MOCK" -> "模拟支付";
            default -> payChannel;
        };
    }

    /**
     * 根据试用天数生成套餐角标。
     */
    public static String packageBadge(Integer trialDays) {
        if (trialDays != null && trialDays > 0) {
            return trialDays + " 天试用";
        }
        return null;
    }

    /**
     * 原价高于现价时返回营销高亮文案。
     */
    public static String packageHighlight(Long originalPriceFen, Long priceFen) {
        if (originalPriceFen != null && priceFen != null && originalPriceFen > priceFen) {
            return "限时优惠";
        }
        return null;
    }

    /**
     * 按总额 10% 计算额度预警阈值（最低 100 Token）。
     */
    public static Long quotaWarningThreshold(long total) {
        if (total <= 0) {
            return 0L;
        }
        return Math.max(100L, Math.round(total * 0.1));
    }
}
