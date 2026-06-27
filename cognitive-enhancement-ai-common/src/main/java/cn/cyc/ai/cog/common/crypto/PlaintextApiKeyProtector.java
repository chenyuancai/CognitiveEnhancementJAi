package cn.cyc.ai.cog.common.crypto;

/**
 * 明文透传（测试或未启用加密时使用）。
 */
public class PlaintextApiKeyProtector implements ApiKeyProtector {

    @Override
    public String protect(String plainApiKey) {
        return plainApiKey;
    }

    @Override
    public String reveal(String storedApiKey) {
        return storedApiKey;
    }
}
