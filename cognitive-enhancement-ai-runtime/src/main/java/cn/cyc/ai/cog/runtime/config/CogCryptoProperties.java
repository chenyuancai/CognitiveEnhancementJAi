package cn.cyc.ai.cog.runtime.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * API Key 等敏感字段加密配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@ConfigurationProperties(prefix = "cog.crypto")
public class CogCryptoProperties {

    /**
     * 是否对提供商 API Key 启用 AES 加密存储。
     */
    private boolean apiKeyEncryptionEnabled = true;

    /**
     * 主密钥（生产环境请通过环境变量或密钥管理系统注入，勿提交仓库）。
     */
    private String masterKey = "cognitive-enhancement-dev-master-key!!";

    /**
     * 判断是否为Api键Encryption是否启用。
     * @return 是否满足条件
     */
    public boolean isApiKeyEncryptionEnabled() {
        return apiKeyEncryptionEnabled;
    }

    /**
     * 设置Api键Encryption是否启用。
     *
     * @param apiKeyEncryptionEnabled api键Encryption是否启用
     */
    public void setApiKeyEncryptionEnabled(boolean apiKeyEncryptionEnabled) {
        this.apiKeyEncryptionEnabled = apiKeyEncryptionEnabled;
    }

    /**
     * 获取Master键。
     * @return Master键
     */
    public String getMasterKey() {
        return masterKey;
    }

    /**
     * 设置Master键。
     *
     * @param masterKey master键
     */
    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }
}
