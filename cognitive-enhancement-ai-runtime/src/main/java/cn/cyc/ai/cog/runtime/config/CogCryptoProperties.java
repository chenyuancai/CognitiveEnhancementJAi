package cn.cyc.ai.cog.runtime.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * API Key 等敏感字段加密配置。
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

    public boolean isApiKeyEncryptionEnabled() {
        return apiKeyEncryptionEnabled;
    }

    public void setApiKeyEncryptionEnabled(boolean apiKeyEncryptionEnabled) {
        this.apiKeyEncryptionEnabled = apiKeyEncryptionEnabled;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }
}
