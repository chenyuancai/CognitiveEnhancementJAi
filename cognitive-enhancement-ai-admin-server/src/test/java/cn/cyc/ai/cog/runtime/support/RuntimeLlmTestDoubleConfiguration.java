package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.center.model.catalog.ModelCatalogRepository;
import cn.cyc.ai.cog.center.model.provider.ModelProviderDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import org.springframework.boot.ApplicationRunner;
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
     * 为 Runtime 集成测试补齐演示模型提供商 Key。
     *
     * @param modelCatalogRepository 模型目录仓储
     * @return 启动后初始化器
     */
    @Bean
    ApplicationRunner runtimeTestModelProviderKeyInitializer(ModelCatalogRepository modelCatalogRepository) {
        return args -> {
            saveProviderKey(modelCatalogRepository, "openai", "OpenAI", "OPENAI_COMPATIBLE",
                    "https://api.openai.com/v1/chat/completions", "sk-test-openai");
            saveProviderKey(modelCatalogRepository, "bailian", "阿里云百炼", "DASHSCOPE",
                    "https://dashscope.aliyuncs.com/compatible-mode/v1", "sk-test-bailian");
        };
    }

    /**
     * 提供固定凭证解析器。
     *
     * @return 固定凭证解析器
     */
    @Bean
    @Primary
    LlmCredentialResolver fixedLlmCredentialResolver() {
        return apiKey -> "resolved-" + apiKey;
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
                      ],
                      "usage": {
                        "prompt_tokens": 12,
                        "completion_tokens": 8,
                        "total_tokens": 20
                      }
                    }
                    """);
        };
    }

    private static void saveProviderKey(ModelCatalogRepository repository,
                                        String providerCode,
                                        String providerName,
                                        String providerType,
                                        String endpoint,
                                        String apiKey) {
        ModelProviderDefinition existing = repository.findProviderByCode(providerCode).orElse(null);
        repository.saveProvider(new ModelProviderDefinition(
                providerCode,
                existing == null ? providerName : existing.providerName(),
                existing == null ? providerType : existing.providerType(),
                existing == null ? endpoint : existing.defaultEndpoint(),
                apiKey,
                existing == null ? "Runtime 集成测试提供商" : existing.description(),
                existing == null ? CommonStatus.ENABLED : existing.status()
        ));
    }
}
