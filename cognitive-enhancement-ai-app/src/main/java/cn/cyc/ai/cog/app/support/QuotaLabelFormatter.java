package cn.cyc.ai.cog.app.support;

/**
 * 额度展示标签格式化。
 *
 * @author cyc
 * @date 2026/6/29
 */
public final class QuotaLabelFormatter {

    private QuotaLabelFormatter() {
    }

    /**
     * 将剩余 Token 格式化为 K/M 简写。
     */
    public static String format(long remaining) {
        if (remaining >= 1_000_000) {
            return (remaining / 1_000_000) + "M";
        }
        if (remaining >= 1_000) {
            return (remaining / 1_000) + "K";
        }
        return String.valueOf(remaining);
    }
}
