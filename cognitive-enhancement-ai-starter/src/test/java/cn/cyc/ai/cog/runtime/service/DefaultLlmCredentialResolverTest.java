package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 默认 LLM 凭证解析器测试。
 *
 * @author cyc
 */
class DefaultLlmCredentialResolverTest {

    /**
     * 待测试的凭证解析器。
     */
    private final DefaultLlmCredentialResolver resolver = new DefaultLlmCredentialResolver();

    /**
     * 验证系统属性中的凭证可以被正确解析。
     */
    @Test
    void shouldResolveCredentialFromSystemProperty() {
        System.setProperty("credential.bailian.test", "test-bailian-key");
        try {
            assertEquals("test-bailian-key", resolver.resolve("credential.bailian.test"));
        } finally {
            System.clearProperty("credential.bailian.test");
        }
    }

    /**
     * 验证普通凭证引用在未命中环境时会回退为原值。
     */
    @Test
    void shouldFallbackToRawCredentialReference() {
        assertEquals("plain-secret", resolver.resolve("plain-secret"));
    }

    /**
     * 验证 env 前缀引用在环境变量缺失时会显式失败。
     */
    @Test
    void shouldRejectEnvCredentialWhenEnvironmentVariableIsMissing() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> resolver.resolve("env:NOT_EXISTING_DASHSCOPE_KEY"));
        assertEquals("未找到环境变量凭证: NOT_EXISTING_DASHSCOPE_KEY", exception.getMessage());
    }
}
