package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 默认 LLM 凭证解析器：API Key 已由 Center 提供商元数据解析，此处仅校验非空。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class DefaultLlmCredentialResolver implements LlmCredentialResolver {

    /**
     * 执行resolve。
     *
     * @param apiKey api键
     * @return 执行结果
     */
    @Override
    public String resolve(String apiKey) {
        if (!StringUtils.hasText(apiKey)) {
            throw new BusinessException("INVALID_ARGUMENT",
                    "模型 API Key 未配置，请在 AI 控制台 → 提供商管理中设置 apiKey");
        }
        return apiKey.trim();
    }
}
