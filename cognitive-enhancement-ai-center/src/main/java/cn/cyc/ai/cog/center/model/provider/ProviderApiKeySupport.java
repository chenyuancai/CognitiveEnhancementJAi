package cn.cyc.ai.cog.center.model.provider;

import org.springframework.util.StringUtils;

/**
 * 提供商 API Key 展示掩码工具。
 */
public final class ProviderApiKeySupport {

    private ProviderApiKeySupport() {
    }

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
