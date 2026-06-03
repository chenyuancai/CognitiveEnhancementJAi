package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.config.DashscopeProperties;
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
     * 百炼配置。
     */
    private final DashscopeProperties dashscopeProperties;

    public DefaultLlmCredentialResolver(DashscopeProperties dashscopeProperties) {
        this.dashscopeProperties = dashscopeProperties;
    }

    /**
     * 解析凭证引用。
     *
     * @param credentialRef 凭证引用
     * @return 解析后的凭证值
     */
    @Override
    public String resolve(String credentialRef) {
        if (!StringUtils.hasText(credentialRef)) {
            if (StringUtils.hasText(dashscopeProperties.getApiKey())) {
                log.info("凭证引用为空，使用 DashscopeProperties fallback");
                return dashscopeProperties.getApiKey();
            }
            throw new BusinessException("INVALID_ARGUMENT", "模型凭证引用不能为空且未配置 DASHSCOPE_API_KEY");
        }
        if (credentialRef.startsWith(ENV_PREFIX)) {
            String environmentKey = credentialRef.substring(ENV_PREFIX.length());
            String environmentValue = System.getenv(environmentKey);
            if (!StringUtils.hasText(environmentValue)) {
                if (StringUtils.hasText(dashscopeProperties.getApiKey())) {
                    log.info("环境变量凭证未找到: {}，使用 DashscopeProperties fallback", environmentKey);
                    return dashscopeProperties.getApiKey();
                }
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
        if (StringUtils.hasText(dashscopeProperties.getApiKey())) {
            log.info("凭证引用未命中环境变量或系统属性，使用 DashscopeProperties fallback, credentialRef={}", credentialRef);
            return dashscopeProperties.getApiKey();
        }
        log.info("凭证引用未命中环境变量或系统属性，将直接使用引用值, credentialRef={}", credentialRef);
        return credentialRef;
    }
}
