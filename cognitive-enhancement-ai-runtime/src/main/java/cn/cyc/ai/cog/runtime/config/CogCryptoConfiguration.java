package cn.cyc.ai.cog.runtime.config;

import cn.cyc.ai.cog.common.crypto.AesApiKeyProtector;
import cn.cyc.ai.cog.common.crypto.ApiKeyProtector;
import cn.cyc.ai.cog.common.crypto.PlaintextApiKeyProtector;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API Key 加密 Bean 配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@EnableConfigurationProperties(CogCryptoProperties.class)
public class CogCryptoConfiguration {

    /**
     * 执行api键Protector。
     *
     * @param properties properties
     * @return 执行结果
     */
    @Bean
    public ApiKeyProtector apiKeyProtector(CogCryptoProperties properties) {
        if (!properties.isApiKeyEncryptionEnabled()) {
            return new PlaintextApiKeyProtector();
        }
        return new AesApiKeyProtector(properties.getMasterKey());
    }
}
