package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import cn.cyc.ai.cog.core.trace.TraceIdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 模型连通性检查控制器测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ModelConnectivityControllerTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 模型定义仓储。
     */
    @Autowired
    private ModelDefinitionRepository modelDefinitionRepository;

    /**
     * 验证百炼模型检查可以返回成功结果。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldCheckSeededBailianModelSuccessfully() throws Exception {
        mockMvc.perform(post("/api/runtime/models/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "qwen-plus",
                                  "prompt": "请回复：百炼检查成功。",
                                  "parameters": {
                                    "temperature": 0.2
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.reachable", is(true)))
                .andExpect(jsonPath("$.data.providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.modelCode", is("qwen-plus")))
                .andExpect(jsonPath("$.data.mock", is(false)))
                .andExpect(jsonPath("$.data.answerPreview", is("这是百炼返回的演示回答。")))
                .andExpect(jsonPath("$.data.latencyMs", notNullValue()));
    }

    /**
     * 验证不受支持的 provider 会返回结构化失败结果。
     *
     * @throws Exception 测试异常
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldReturnFailureResultWhenProviderIsUnsupported() throws Exception {
        modelDefinitionRepository.save(new ModelDefinition(
                "unsupported-provider",
                "Unsupported Provider",
                "model.unsupported",
                "Unsupported Model",
                "CHAT",
                "https://mock.local/llm",
                "mock-secret",
                30_000,
                1,
                CommonStatus.ENABLED,
                100,
                null
        ));

        mockMvc.perform(post("/api/runtime/models/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "model.unsupported"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.reachable", is(false)))
                .andExpect(jsonPath("$.data.providerCode", is("unsupported-provider")))
                .andExpect(jsonPath("$.data.failureReason", is("未找到可用的 LLM Provider 处理器: unsupported-provider")));
    }

    /**
     * 验证可以查询模型状态摘要列表。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldListModelStatusesWithLatestCheckSummary() throws Exception {
        mockMvc.perform(post("/api/runtime/models/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "qwen-plus",
                                  "prompt": "请回复：百炼状态检查成功。"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reachable", is(true)));
        modelDefinitionRepository.save(new ModelDefinition(
                "unsupported-provider",
                "Unsupported Provider",
                "model.unsupported",
                "Unsupported Model",
                "CHAT",
                "https://mock.local/llm",
                "mock-secret",
                30_000,
                1,
                CommonStatus.ENABLED,
                100,
                null
        ));
        modelDefinitionRepository.save(new ModelDefinition(
                "disabled-provider",
                "Disabled Provider",
                "model.disabled",
                "Disabled Model",
                "CHAT",
                "https://mock.local/llm",
                "mock-secret",
                30_000,
                1,
                CommonStatus.DISABLED,
                1,
                null
        ));
        mockMvc.perform(post("/api/runtime/models/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "model.unsupported"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reachable", is(false)));
        mockMvc.perform(post("/api/runtime/models/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "model.unsupported"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reachable", is(false)));

        mockMvc.perform(get("/api/runtime/models/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(3)))
                .andExpect(jsonPath("$.data.items[0].providerCode", is("unsupported-provider")))
                .andExpect(jsonPath("$.data.items[0].modelCode", is("model.unsupported")))
                .andExpect(jsonPath("$.data.items[0].hasCheckRecord", is(true)))
                .andExpect(jsonPath("$.data.items[0].reachable", is(false)))
                .andExpect(jsonPath("$.data.items[0].healthStatus", is("UNREACHABLE")))
                .andExpect(jsonPath("$.data.items[0].lastSuccessAt", nullValue()))
                .andExpect(jsonPath("$.data.items[0].lastFailureAt", notNullValue()))
                .andExpect(jsonPath("$.data.items[0].consecutiveFailureCount", is(2)))
                .andExpect(jsonPath("$.data.items[1].providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.items[1].providerName", is("阿里云百炼")))
                .andExpect(jsonPath("$.data.items[1].modelCode", is("qwen-plus")))
                .andExpect(jsonPath("$.data.items[1].status", is("ENABLED")))
                .andExpect(jsonPath("$.data.items[1].hasCheckRecord", is(true)))
                .andExpect(jsonPath("$.data.items[1].reachable", is(true)))
                .andExpect(jsonPath("$.data.items[1].healthStatus", is("REACHABLE")))
                .andExpect(jsonPath("$.data.items[1].mock", is(false)))
                .andExpect(jsonPath("$.data.items[1].answerPreview", is("这是百炼返回的演示回答。")))
                .andExpect(jsonPath("$.data.items[1].lastSuccessAt", notNullValue()))
                .andExpect(jsonPath("$.data.items[1].lastFailureAt", nullValue()))
                .andExpect(jsonPath("$.data.items[1].consecutiveFailureCount", is(0)))
                .andExpect(jsonPath("$.data.items[2].providerCode", is("openai")))
                .andExpect(jsonPath("$.data.items[2].modelCode", is("gpt-4o-mini")))
                .andExpect(jsonPath("$.data.items[2].hasCheckRecord", is(false)))
                .andExpect(jsonPath("$.data.items[2].reachable", nullValue()))
                .andExpect(jsonPath("$.data.items[2].healthStatus", is("UNCHECKED")))
                .andExpect(jsonPath("$.data.items[2].lastSuccessAt", nullValue()))
                .andExpect(jsonPath("$.data.items[2].lastFailureAt", nullValue()))
                .andExpect(jsonPath("$.data.items[2].consecutiveFailureCount", is(0)));

        mockMvc.perform(get("/api/runtime/models/statuses")
                        .param("providerCode", "bailian")
                        .param("modelCode", "qwen-plus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.items[0].modelCode", is("qwen-plus")))
                .andExpect(jsonPath("$.data.items[0].hasCheckRecord", is(true)))
                .andExpect(jsonPath("$.data.items[0].consecutiveFailureCount", is(0)));
    }

    /**
     * 验证可以刷新单个和多个模型状态。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRefreshSingleAndMultipleModelStatuses() throws Exception {
        modelDefinitionRepository.save(new ModelDefinition(
                "unsupported-provider",
                "Unsupported Provider",
                "model.unsupported",
                "Unsupported Model",
                "CHAT",
                "https://mock.local/llm",
                "mock-secret",
                30_000,
                1,
                CommonStatus.ENABLED,
                100,
                null
        ));

        mockMvc.perform(post("/api/runtime/models/statuses/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "qwen-plus",
                                  "prompt": "请回复：单模型刷新成功。"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].reachable", is(true)))
                .andExpect(jsonPath("$.data.items[0].providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.items[0].modelCode", is("qwen-plus")));

        mockMvc.perform(post("/api/runtime/models/statuses/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCodes": ["qwen-plus", "model.unsupported"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(2)))
                .andExpect(jsonPath("$.data.items[0].modelCode", is("qwen-plus")))
                .andExpect(jsonPath("$.data.items[0].reachable", is(true)))
                .andExpect(jsonPath("$.data.items[1].modelCode", is("model.unsupported")))
                .andExpect(jsonPath("$.data.items[1].reachable", is(false)))
                .andExpect(jsonPath("$.data.items[1].failureReason", is("未找到可用的 LLM Provider 处理器: unsupported-provider")));
    }

    /**
     * 验证未指定模型编码时会刷新全部已启用模型。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRefreshAllEnabledModelsWhenNoModelCodeSpecified() throws Exception {
        modelDefinitionRepository.save(new ModelDefinition(
                "disabled-provider",
                "Disabled Provider",
                "model.disabled",
                "Disabled Model",
                "CHAT",
                "https://mock.local/llm",
                "mock-secret",
                30_000,
                1,
                CommonStatus.DISABLED,
                1,
                null
        ));

        mockMvc.perform(post("/api/runtime/models/statuses/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "prompt": "请回复：全量刷新成功。"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(2)))
                .andExpect(jsonPath("$.data.items[0].modelCode", is("gpt-4o-mini")))
                .andExpect(jsonPath("$.data.items[0].reachable", is(true)))
                .andExpect(jsonPath("$.data.items[1].modelCode", is("qwen-plus")))
                .andExpect(jsonPath("$.data.items[1].reachable", is(true)));
    }

    /**
     * 验证可以查询模型状态总览与失败聚合信息。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldQueryModelStatusOverviewWithFailureAggregation() throws Exception {
        modelDefinitionRepository.save(new ModelDefinition(
                "unsupported-provider",
                "Unsupported Provider",
                "model.unsupported",
                "Unsupported Model",
                "CHAT",
                "https://mock.local/llm",
                "mock-secret",
                30_000,
                1,
                CommonStatus.ENABLED,
                100,
                null
        ));
        modelDefinitionRepository.save(new ModelDefinition(
                "disabled-provider",
                "Disabled Provider",
                "model.disabled",
                "Disabled Model",
                "CHAT",
                "https://mock.local/llm",
                "mock-secret",
                30_000,
                1,
                CommonStatus.DISABLED,
                1,
                null
        ));

        mockMvc.perform(post("/api/runtime/models/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "qwen-plus",
                                  "prompt": "请回复：总览检查成功。"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reachable", is(true)));

        mockMvc.perform(post("/api/runtime/models/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "model.unsupported"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reachable", is(false)));

        mockMvc.perform(get("/api/runtime/models/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalModels", is(3)))
                .andExpect(jsonPath("$.data.enabledModels", is(3)))
                .andExpect(jsonPath("$.data.disabledModels", is(0)))
                .andExpect(jsonPath("$.data.checkedModels", is(2)))
                .andExpect(jsonPath("$.data.reachableModels", is(1)))
                .andExpect(jsonPath("$.data.unreachableModels", is(1)))
                .andExpect(jsonPath("$.data.uncheckedModels", is(1)))
                .andExpect(jsonPath("$.data.lastCheckedAt", notNullValue()))
                .andExpect(jsonPath("$.data.lastSuccessAt", notNullValue()))
                .andExpect(jsonPath("$.data.lastFailureAt", notNullValue()))
                .andExpect(jsonPath("$.data.failureSummaries[0].reason", is("未找到可用的 LLM Provider 处理器: unsupported-provider")))
                .andExpect(jsonPath("$.data.failureSummaries[0].count", is(1)))
                .andExpect(jsonPath("$.data.failureSummaries[0].affectedModelCodes[0]", is("model.unsupported")));
    }

    /**
     * 固定 traceId 和外部依赖的测试配置。
     *
     * @author cyc
     */
    @TestConfiguration
    static class RuntimeTraceConfiguration {

        /**
         * 提供固定 traceId 生成器。
         *
         * @return 固定 traceId 生成器
         */
        @Bean
        @Primary
        TraceIdGenerator fixedTraceIdGenerator() {
            return () -> "trace-test-model-check-001";
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
         * 提供 HTTP 执行器测试替身。
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
}
