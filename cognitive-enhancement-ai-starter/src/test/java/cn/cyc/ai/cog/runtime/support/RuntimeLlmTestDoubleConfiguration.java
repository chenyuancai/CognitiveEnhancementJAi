package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runtime LLM 测试替身配置。
 *
 * @author cyc
 */
@TestConfiguration
public class RuntimeLlmTestDoubleConfiguration {

    /**
     * 提供固定凭证解析器。
     *
     * @return 固定凭证解析器
     */
    @Bean
    @Primary
    LlmCredentialResolver fixedLlmCredentialResolver() {
        return credentialRef -> "resolved-" + credentialRef;
    }

    /**
     * 提供百炼 HTTP 执行器测试替身。
     *
     * @return HTTP 执行器替身
     */
    @Bean
    @Primary
    LlmHttpExecutor fakeLlmHttpExecutor() {
        return request -> {
            assertTrue(request.url().endsWith("/chat/completions"));
            return new LlmHttpResponse(200, """
                    {
                      "choices": [
                        {
                          "message": {
                            "content": "这是百炼返回的演示回答。"
                          }
                        }
                      ]
                    }
                    """);
        };
    }
}
