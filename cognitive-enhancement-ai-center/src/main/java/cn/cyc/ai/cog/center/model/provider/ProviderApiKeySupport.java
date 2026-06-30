package cn.cyc.ai.cog.center.model.provider;

import org.springframework.util.StringUtils;

/**
 * 提供商 API Key 展示掩码工具。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class ProviderApiKeySupport {

    /**
     * 创建提供者Api键支持工具。
     */
    private ProviderApiKeySupport() {
    }

    /**
     * 判断是否为Configured。
     *
     * @param apiKey api键
     * @return 是否满足条件
     */
    public static boolean isConfigured(String apiKey) {
        return isEffectiveApiKey(apiKey);
    }

    /**
     * 是否为可用于 HTTP 鉴权的 API Key（排除历史种子占位符 credential_ref）。
     */
    public static boolean isEffectiveApiKey(String apiKey) {
        if (!StringUtils.hasText(apiKey)) {
            return false;
        }
        String trimmed = apiKey.trim();
        return !(trimmed.startsWith("__") && trimmed.endsWith("__"));
    }

    /**
     * 执行mask。
     *
     * @param apiKey api键
     * @return 执行结果
     */
    public static String mask(String apiKey) {
        if (!StringUtils.hasText(apiKey)) {
            return "";
        }
        String trimmed = apiKey.trim();
        if (trimmed.length() <= 4) {
            return "****";
        }
        return "****" + trimmed.substring(trimmed.length() - 4);
    }
}
