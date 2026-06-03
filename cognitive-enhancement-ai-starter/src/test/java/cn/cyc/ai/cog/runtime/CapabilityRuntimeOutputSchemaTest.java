package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.trace.TraceIdGenerator;
import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 输出 Schema 校验集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
class CapabilityRuntimeOutputSchemaTest {

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
     * LLM 网关 mock。
     */
    @MockBean
    private LlmGateway llmGateway;

    /**
     * 验证 LLM 业务输出不符合 Capability 输出 Schema 时会在返回前被拦截。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRejectCapabilityWhenBusinessOutputViolatesSchema() throws Exception {
        modelDefinitionRepository.save(new ModelDefinition(
                "openai",
                "OpenAI",
                "gpt-4o-mini",
                "GPT-4o mini",
                "chat",
                "https://api.openai.com/v1/chat/completions",
                "credential/openai/default",
                30000,
                1,
                CommonStatus.ENABLED,
                100,
                null
        ));
        when(llmGateway.generate(any(ExecutionContext.class), any(ModelDefinition.class), any()))
                .thenReturn(new LlmInvocationResult(
                        "LLM",
                        "openai",
                        "gpt-4o-mini",
                        "prompt.chat.default",
                        "请以助手身份直接回答用户问题：输出 Schema 会校验吗？",
                        null,
                        Map.of(),
                        true
                ));

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate",
                                  "input": {
                                    "question": "输出 Schema 会校验吗？"
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0400")))
                .andExpect(jsonPath("$.message", is("输出参数 answer 不能为空")));
    }

    /**
     * 固定 traceId 的测试配置。
     *
     * @author cyc
     */
    @TestConfiguration
    static class RuntimeTraceConfiguration {

        /**
         * 提供固定 traceId 生成器，方便断言。
         *
         * @return 固定 traceId 生成器
         */
        @Bean
        @Primary
        TraceIdGenerator fixedTraceIdGenerator() {
            return () -> "trace-test-output-schema-001";
        }
    }
}
