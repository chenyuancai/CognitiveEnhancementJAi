package cn.cyc.ai.cog.runtime.support;

/**
 * 基于文本长度的 token 数粗估，用于 mock 降级场景。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class TokenCountEstimator {

    /**
     * 创建TokenCountEstimator。
     */
    private TokenCountEstimator() {
    }

    /**
     * 按字符数粗估 token（约 4 字符 1 token）。
     *
     * @param text 文本
     * @return 估算 token 数
     */
    public static int estimate(Object text) {
        if (text == null) {
            return 0;
        }
        String value = String.valueOf(text);
        if (value.isBlank()) {
            return 0;
        }
        return Math.max(1, value.length() / 4);
    }
}
