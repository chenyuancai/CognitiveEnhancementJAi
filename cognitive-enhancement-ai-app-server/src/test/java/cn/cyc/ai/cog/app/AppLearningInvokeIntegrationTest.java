package cn.cyc.ai.cog.app;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("app-it")
@Import(AppLearningInvokeIntegrationTest.LlmTestDoubleConfiguration.class)
class AppLearningInvokeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldInvokeQaLearningMode() throws Exception {
        mockMvc.perform(post("/api/app/learning/invoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mode": "QA",
                                  "input": {"question": "hello"},
                                  "parameters": {}
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.traceId").exists());
    }

    @TestConfiguration
    static class LlmTestDoubleConfiguration {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        ApplicationRunner appLearningModelProviderKeyInitializer(ApplicationContext applicationContext) {
            return args -> {
                saveProviderKey(applicationContext, "openai", "OpenAI", "OPENAI_COMPATIBLE",
                        "https://api.openai.com/v1/chat/completions", "sk-test-openai");
                saveProviderKey(applicationContext, "bailian", "阿里云百炼", "DASHSCOPE",
                        "https://dashscope.aliyuncs.com/compatible-mode/v1", "sk-test-bailian");
            };
        }

        @Bean
        @Primary
        LlmCredentialResolver fixedLlmCredentialResolver() {
            return apiKey -> "resolved-" + apiKey;
        }

        @Bean
        @Primary
        LlmHttpExecutor fakeLlmHttpExecutor() {
            return request -> {
                assertTrue(request.url().endsWith("/chat/completions"));
                assertTrue(request.headers().get("Authorization").startsWith("Bearer resolved-"));
                return new LlmHttpResponse(200, """
                        {
                          "choices": [
                            {
                              "message": {
                                "content": "这是一期 mock LLM 输出，后续可接入真实 provider。"
                              }
                            }
                          ],
                          "usage": {
                            "prompt_tokens": 10,
                            "completion_tokens": 8,
                            "total_tokens": 18
                          }
                        }
                        """);
            };
        }

        private static void saveProviderKey(ApplicationContext applicationContext,
                                            String providerCode,
                                            String providerName,
                                            String providerType,
                                            String endpoint,
                                            String apiKey) throws ReflectiveOperationException {
            Object service = applicationContext.getBean("modelProviderAdminService");
            Class<?> requestType = Class.forName("cn.cyc.ai.cog.center.model.provider.ModelProviderUpsertRequest");
            Object request = requestType.getConstructor(
                            String.class,
                            String.class,
                            String.class,
                            String.class,
                            String.class,
                            String.class,
                            CommonStatus.class)
                    .newInstance(
                            providerCode,
                            providerName,
                            providerType,
                            endpoint,
                            apiKey,
                            "App Learning 集成测试提供商",
                            CommonStatus.ENABLED);
            service.getClass().getMethod("seed", requestType).invoke(service, request);
        }
    }
}
