package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.center.model.catalog.ModelBindingDefinition;
import cn.cyc.ai.cog.center.model.catalog.ModelCatalogRepository;
import cn.cyc.ai.cog.center.model.catalog.ModelMasterDefinition;
import cn.cyc.ai.cog.center.model.provider.ModelProviderDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.model.registry.LlmRouteRegistry;
import cn.cyc.ai.cog.runtime.model.registry.ModelRuntimeRefreshService;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import cn.cyc.ai.cog.core.trace.TraceIdGenerator;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime LLM 分支集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
class CapabilityRuntimeLlmControllerTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 模型目录仓储。
     */
    @Autowired
    private ModelCatalogRepository modelCatalogRepository;

    /**
     * 运行时路由刷新服务。
     */
    @Autowired
    private ModelRuntimeRefreshService modelRuntimeRefreshService;

    /**
     * LLM 运行时路由注册表。
     */
    @Autowired
    private LlmRouteRegistry llmRouteRegistry;

    /**
     * 验证 capability 可以走 Prompt + LLM 主链路。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRouteCapabilityToLlmWithRenderedPrompt() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate",
                                  "input": {
                                    "question": "请用一句话介绍这个项目。"
                                  },
                                  "parameters": {
                                    "temperature": 0.2,
                                    "maxTokens": 256
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.traceId", notNullValue()))
                .andExpect(jsonPath("$.data.traceId", is("trace-test-llm-001")))
                .andExpect(jsonPath("$.data.capability.capabilityCode", is("capability.chat.generate")))
                .andExpect(jsonPath("$.data.agent.agentCode", is("agent.chat")))
                .andExpect(jsonPath("$.data.result.status", is("LLM_GENERATED")))
                .andExpect(jsonPath("$.data.result.output.prompt.resolved", is(true)))
                .andExpect(jsonPath("$.data.result.output.prompt.promptCode", is("prompt.chat.default")))
                .andExpect(jsonPath("$.data.result.output.prompt.renderedPrompt",
                        is("请以助手身份直接回答用户问题：请用一句话介绍这个项目。")))
                .andExpect(jsonPath("$.data.result.output.businessOutput.answer", is("这是一期 mock LLM 输出，后续可接入真实 provider。")))
                .andExpect(jsonPath("$.data.result.output.modelCode", is("gpt-4o-mini")))
                .andExpect(jsonPath("$.data.result.output.providerCode", is("openai")))
                .andExpect(jsonPath("$.data.result.output.parameters.temperature", is(0.2)))
                .andExpect(jsonPath("$.data.result.output.parameters.maxTokens", is(256)))
                .andExpect(jsonPath("$.data.result.output.executorType", is("LLM")))
                .andExpect(jsonPath("$.data.result.output.invocationResult.executorType", is("LLM")))
                .andExpect(jsonPath("$.data.result.output.invocationResult.providerCode", is("openai")))
                .andExpect(jsonPath("$.data.result.output.invocationResult.modelCode", is("gpt-4o-mini")))
                .andExpect(jsonPath("$.data.result.output.invocationResult.parameters.temperature", is(0.2)))
                .andExpect(jsonPath("$.data.result.output.invocationResult.parameters.maxTokens", is(256)))
                .andExpect(jsonPath("$.data.result.output.llmResult.executorType", is("LLM")))
                .andExpect(jsonPath("$.data.result.output.llmResult.providerCode", is("openai")))
                .andExpect(jsonPath("$.data.result.output.llmResult.modelCode", is("gpt-4o-mini")))
                .andExpect(jsonPath("$.data.result.output.llmResult.promptCode", is("prompt.chat.default")))
                .andExpect(jsonPath("$.data.result.output.llmResult.parameters.temperature", is(0.2)))
                .andExpect(jsonPath("$.data.result.output.llmResult.parameters.maxTokens", is(256)))
                .andExpect(jsonPath("$.data.result.output.llmResult.renderedPrompt",
                        is("请以助手身份直接回答用户问题：请用一句话介绍这个项目。")));
    }

    /**
     * 验证模型被禁用时，LLM 能力调用会被显式拒绝。
     *
     * @throws Exception 测试异常
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldRejectLlmExecutionWhenModelIsDisabled() throws Exception {
        llmRouteRegistry.replaceAll(List.of(new ModelDefinition(
                "mock-openai",
                "Mock OpenAI",
                "gpt-4o-mini",
                "GPT-4o mini",
                "chat",
                "https://mock.local/llm",
                "mock-secret",
                30000,
                1,
                CommonStatus.DISABLED,
                100,
                null
        )));

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate",
                                  "input": {
                                    "question": "模型禁用后还能调用吗？"
                                  }
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0409")))
                .andExpect(jsonPath("$.message", is("模型未启用: gpt-4o-mini")));
    }

    /**
     * 验证 provider 不受支持时，LLM 能力调用会被显式拒绝。
     *
     * @throws Exception 测试异常
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldRejectLlmExecutionWhenProviderIsUnsupported() throws Exception {
        llmRouteRegistry.replaceAll(List.of(new ModelDefinition(
                "unsupported-provider",
                "Unsupported Provider",
                "UNSUPPORTED",
                "gpt-4o-mini",
                "GPT-4o mini",
                "chat",
                "https://mock.local/llm",
                "mock-secret",
                30000,
                1,
                CommonStatus.ENABLED,
                100,
                null
        )));

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate",
                                  "input": {
                                    "question": "provider 不支持时怎么办？"
                                  }
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0409")))
                .andExpect(jsonPath("$.message", is("未找到可用的 LLM Provider 处理器: unsupported-provider")));
    }

    /**
     * 验证未知执行参数会在运行时入口被显式拒绝。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRejectLlmExecutionWhenParameterIsUnsupported() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate",
                                  "input": {
                                    "question": "未知参数会怎样？"
                                  },
                                  "parameters": {
                                    "unknownFlag": true
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0400")))
                .andExpect(jsonPath("$.message", is("不支持的执行参数: unknownFlag")));
    }

    /**
     * 验证越界执行参数会按生效后的组合约束在运行时入口被显式拒绝。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRejectLlmExecutionWhenTemperatureIsOutOfRange() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate",
                                  "input": {
                                    "question": "温度越界会怎样？"
                                  },
                                  "parameters": {
                                    "temperature": 3
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0400")))
                .andExpect(jsonPath("$.message", is("temperature 取值范围必须在 0 到 1.5 之间")));
    }

    /**
     * 验证 Agent 可进一步收紧 Capability 的参数上限。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRejectLlmExecutionWhenAgentConstraintIsStricterThanCapability() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate",
                                  "input": {
                                    "question": "Agent 上限会生效吗？"
                                  },
                                  "parameters": {
                                    "temperature": 1.8
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0400")))
                .andExpect(jsonPath("$.message", is("temperature 取值范围必须在 0 到 1.5 之间")));
    }

    /**
     * 验证 capability 可以路由到百炼 provider。
     *
     * @throws Exception 测试异常
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldRouteCapabilityToBailianProvider() throws Exception {
        modelCatalogRepository.saveModel(new ModelMasterDefinition(
                "gpt-4o-mini",
                "GPT-4o mini",
                "chat",
                30000,
                1,
                CommonStatus.ENABLED,
                null,
                List.of(new ModelBindingDefinition(
                        "gpt-4o-mini",
                        "bailian",
                        "https://dashscope.aliyuncs.com/compatible-mode/v1",
                        "test-bailian-key",
                        100,
                        CommonStatus.ENABLED
                ))
        ));
        modelRuntimeRefreshService.refresh();

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate",
                                  "input": {
                                    "question": "请介绍一下百炼接入。"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.result.status", is("LLM_GENERATED")))
                .andExpect(jsonPath("$.data.result.output.mock", is(false)))
                .andExpect(jsonPath("$.data.result.output.providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.result.output.businessOutput.answer", is("这是百炼返回的演示回答。")))
                .andExpect(jsonPath("$.data.result.output.llmResult.providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.result.output.llmResult.mock", is(false)))
                .andExpect(jsonPath("$.data.result.output.llmResult.answer", is("这是百炼返回的演示回答。")));
    }

    /**
     * 验证演示数据中的百炼能力可以直接走通。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRouteSeededBailianCapabilityToBailianProvider() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate.bailian",
                                  "input": {
                                    "question": "请用一句话说明百炼能力入口。"
                                  },
                                  "parameters": {
                                    "temperature": 0.3,
                                    "maxTokens": 512
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.agent.agentCode", is("agent.chat.bailian")))
                .andExpect(jsonPath("$.data.result.status", is("LLM_GENERATED")))
                .andExpect(jsonPath("$.data.result.output.mock", is(false)))
                .andExpect(jsonPath("$.data.result.output.modelCode", is("qwen-plus")))
                .andExpect(jsonPath("$.data.result.output.providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.result.output.businessOutput.answer", is("这是百炼返回的演示回答。")))
                .andExpect(jsonPath("$.data.result.output.llmResult.providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.result.output.llmResult.modelCode", is("qwen-plus")))
                .andExpect(jsonPath("$.data.result.output.llmResult.answer", is("这是百炼返回的演示回答。")));
    }

    /**
     * 固定 traceId 的测试配置。
     *
     * @author cyc
     */
    @TestConfiguration
    static class RuntimeTraceConfiguration {

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
         * 提供固定凭证解析器，避免测试依赖外部环境变量。
         *
         * @return 固定凭证解析器
         */
        @Bean
        @Primary
        LlmCredentialResolver fixedLlmCredentialResolver() {
            return apiKey -> "resolved-" + apiKey;
        }

        /**
         * 提供百炼 HTTP 测试替身。
         *
         * @return HTTP 执行器测试替身
         */
        @Bean
        @Primary
        LlmHttpExecutor fakeLlmHttpExecutor() {
            return request -> {
                assertTrue(request.url().endsWith("/chat/completions"));
                assertTrue(request.headers().get("Authorization").startsWith("Bearer resolved-"));
                assertTrue(request.body().contains("\"model\":"));
                String answer = request.url().contains("dashscope.aliyuncs.com")
                        ? "这是百炼返回的演示回答。"
                        : "这是一期 mock LLM 输出，后续可接入真实 provider。";
                return new LlmHttpResponse(200, """
                        {
                          "choices": [
                            {
                              "message": {
                                "content": "%s"
                              }
                            }
                          ]
                        }
                        """.formatted(answer));
            };
        }

        /**
         * 提供固定 traceId 生成器，方便断言。
         *
         * @return 固定 traceId 生成器
         */
        @Bean
        @Primary
        TraceIdGenerator fixedTraceIdGenerator() {
            return () -> "trace-test-llm-001";
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
}
