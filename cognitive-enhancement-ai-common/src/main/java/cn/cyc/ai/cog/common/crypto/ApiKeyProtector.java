package cn.cyc.ai.cog.common.crypto;

/**
 * 提供商 API Key 存储加解密。
 */
public interface ApiKeyProtector {

    /**
     * 写入 DB 前加密（明文为空则原样返回）。
     */
    String protect(String plainApiKey);

    /**
     * 从 DB 读出后解密（非加密格式则原样返回，兼容历史明文）。
     */
    String reveal(String storedApiKey);

    /**
     * 解密失败时返回 {@code null}（用于绑定级覆盖 Key，可回退到提供商默认 Key）。
     */
    default String tryReveal(String storedApiKey) {
        if (storedApiKey == null || storedApiKey.isBlank()) {
            return storedApiKey;
        }
        try {
            return reveal(storedApiKey);
        } catch (RuntimeException ignored) {
            return null;
        }
    }
}
