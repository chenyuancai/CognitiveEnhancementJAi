package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 默认 LLM 凭证解析器测试。
 */
class DefaultLlmCredentialResolverTest {

    private final DefaultLlmCredentialResolver resolver = new DefaultLlmCredentialResolver();

    @Test
    void shouldReturnTrimmedApiKey() {
        assertEquals("sk-test-key", resolver.resolve("  sk-test-key  "));
    }

    @Test
    void shouldRejectBlankApiKey() {
        BusinessException exception = assertThrows(BusinessException.class, () -> resolver.resolve(" "));
        assertEquals("模型 API Key 未配置，请在 AI 控制台 → 提供商管理中设置 apiKey", exception.getMessage());
    }
}
