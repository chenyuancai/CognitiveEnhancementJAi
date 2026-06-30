package cn.cyc.ai.cog.common.crypto;

/**
 * 明文透传（测试或未启用加密时使用）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class PlaintextApiKeyProtector implements ApiKeyProtector {

    /**
     * 执行protect。
     *
     * @param plainApiKey plainApi键
     * @return 执行结果
     */
    @Override
    public String protect(String plainApiKey) {
        return plainApiKey;
    }

    /**
     * 执行reveal。
     *
     * @param storedApiKey storedApi键
     * @return 执行结果
     */
    @Override
    public String reveal(String storedApiKey) {
        return storedApiKey;
    }
}
