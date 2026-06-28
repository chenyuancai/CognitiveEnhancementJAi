package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.trace.TraceIdGenerator;
import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import cn.cyc.ai.cog.runtime.support.RuntimeLlmTestDoubleConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PH5 治理链路集成测试：规划、自反思、计划驱动 Tool 选择。
 */
@SpringBootTest(properties = "cog.runtime.react.enabled=false")
@AutoConfigureMockMvc
@Import(RuntimeLlmTestDoubleConfiguration.class)
class CapabilityRuntimePh5GovernanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LlmGateway llmGateway;

    @BeforeEach
    void setUpDefaultLlmMock() {
        when(llmGateway.generate(any(ExecutionContext.class), any(ModelDefinition.class), any()))
                .thenReturn(defaultLlmResult());
    }

    @Test
    void shouldReturnTaskPlanWhenPlanningEnabled() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "PH5 规划是否生效?"
                                  },
                                  "parameters": {
                                    "planningEnabled": true,
                                    "planningMode": "RULE"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.result.output.taskPlan.goal", is("PH5 规划是否生效?")))
                .andExpect(jsonPath("$.data.result.output.taskPlan.steps[0].status", is("DONE")))
                .andExpect(jsonPath("$.data.result.output.taskPlan.steps[1].action", is("TOOL")))
                .andExpect(jsonPath("$.data.result.output.planDrivenToolSelection", is(true)))
                .andExpect(jsonPath("$.data.result.output.selectedToolCode", is("tool.search")));
    }

    @Test
    void shouldSelectPreferredToolWhenPlanDrivenToolEnabled() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "Echo Tool 是否被选中?"
                                  },
                                  "parameters": {
                                    "planningEnabled": true,
                                    "preferredToolCode": "tool.echo"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.result.output.selectedToolCode", is("tool.echo")))
                .andExpect(jsonPath("$.data.result.output.invocationResult.toolCode", is("tool.echo")));
    }

    @Test
    void shouldApplyReflectionRetryWhenInitialAnswerIsTooShort() throws Exception {
        AtomicInteger callCount = new AtomicInteger();
        when(llmGateway.generate(any(ExecutionContext.class), any(ModelDefinition.class), any()))
                .thenAnswer(invocation -> {
                    int current = callCount.incrementAndGet();
                    String answer = current == 1 ? "短答" : "这是经过反思重试后的完整回答。";
                    return new LlmInvocationResult(
                            "LLM",
                            "openai",
                            "gpt-4o-mini",
                            "prompt.chat.default",
                            "rendered",
                            answer,
                            Map.of(),
                            5,
                            10,
                            15,
                            20L,
                            true);
                });

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate",
                                  "input": {
                                    "question": "触发反思"
                                  },
                                  "parameters": {
                                    "reflectionEnabled": true
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.result.output.reflectionApplied", is(true)))
                .andExpect(jsonPath("$.data.result.output.reflectionRetryCount", is(1)))
                .andExpect(jsonPath("$.data.result.output.businessOutput.answer",
                        is("这是经过反思重试后的完整回答。")));
    }

    private LlmInvocationResult defaultLlmResult() {
        return new LlmInvocationResult(
                "LLM",
                "openai",
                "gpt-4o-mini",
                "prompt.qa.default",
                "rendered",
                "这是一期 mock LLM 输出，后续可接入真实 provider。",
                Map.of(),
                5,
                10,
                15,
                20L,
                true);
    }

    @TestConfiguration
    static class Ph5TraceConfiguration {

        @Bean
        @Primary
        TraceIdGenerator fixedTraceIdGenerator() {
            return () -> "trace-test-ph5-001";
        }
    }
}
