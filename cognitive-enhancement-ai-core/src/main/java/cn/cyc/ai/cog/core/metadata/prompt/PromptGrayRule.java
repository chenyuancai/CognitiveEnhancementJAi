package cn.cyc.ai.cog.core.metadata.prompt;

/**
 * Prompt 灰度规则（简版百分比策略）。
 *
 * @param strategy         策略类型，默认 PERCENTAGE
 * @param baselineVersion  基线版本
 * @param candidateVersion 候选版本
 * @param percentage       候选流量百分比 0~100
 * @param hashKey          哈希键来源，默认 traceId
 * @author cyc
 */
public record PromptGrayRule(
        String strategy,
        String baselineVersion,
        String candidateVersion,
        int percentage,
        String hashKey
) {

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
