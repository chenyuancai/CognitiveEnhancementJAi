package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 默认 LLM 凭证解析器。
 *
 * @author cyc
 */
@Component
public class DefaultLlmCredentialResolver implements LlmCredentialResolver {

    /**
     * 凭证解析日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultLlmCredentialResolver.class);

    /**
     * 环境变量前缀。
     */
    private static final String ENV_PREFIX = "env:";

    /**
     * 解析凭证引用。
     *
     * @param credentialRef 凭证引用
     * @return 解析后的凭证值
     */
    @Override
    public String resolve(String credentialRef) {
        if (!StringUtils.hasText(credentialRef)) {
            throw new BusinessException("INVALID_ARGUMENT", "模型凭证引用不能为空");
        }
        if (credentialRef.startsWith(ENV_PREFIX)) {
            String environmentKey = credentialRef.substring(ENV_PREFIX.length());
            String environmentValue = System.getenv(environmentKey);
            if (!StringUtils.hasText(environmentValue)) {
                throw new BusinessException("CONFLICT", "未找到环境变量凭证: " + environmentKey);
            }
            return environmentValue;
        }
        String propertyValue = System.getProperty(credentialRef);
        if (StringUtils.hasText(propertyValue)) {
            return propertyValue;
        }
        String environmentValue = System.getenv(credentialRef);
        if (StringUtils.hasText(environmentValue)) {
            return environmentValue;
        }
        log.info("凭证引用未命中环境变量或系统属性，将直接使用引用值, credentialRef={}", credentialRef);
        return credentialRef;
    }
}
