package cn.cyc.ai.cog.core.metadata.prompt;

/**
 * Prompt 灰度规则（简版百分比策略）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record PromptGrayRule(
        String strategy,
        String baselineVersion,
        String candidateVersion,
        int percentage,
        String hashKey
) {

    /** 策略PERCENTAGE。 */
    public static final String STRATEGY_PERCENTAGE = "PERCENTAGE";

    public PromptGrayRule {
        if (strategy == null || strategy.isBlank()) {
            strategy = STRATEGY_PERCENTAGE;
        }
        if (hashKey == null || hashKey.isBlank()) {
            hashKey = "traceId";
        }
        if (percentage < 0) {
            percentage = 0;
        }
        if (percentage > 100) {
            percentage = 100;
        }
    }

    /**
     * 构造默认灰度规则。
     *
     * @param baselineVersion  基线版本
     * @param candidateVersion 候选版本
     * @param percentage       候选百分比
     */
    public PromptGrayRule(String baselineVersion, String candidateVersion, int percentage) {
        this(STRATEGY_PERCENTAGE, baselineVersion, candidateVersion, percentage, "traceId");
    }
}
