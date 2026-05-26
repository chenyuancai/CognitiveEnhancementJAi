package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import cn.cyc.ai.cog.core.trace.TraceIdGenerator;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 能力入口集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
class CapabilityRuntimeControllerTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 验证 capability 可以路由到绑定 agent，并继续触发 mock Tool 执行。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRouteCapabilityToBoundAgentAndReturnTraceId() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "什么是 Cognitive Enhancement AI?"
                                  },
                                  "parameters": {
                                    "temperature": 0.3,
                                    "topP": 0.5
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.traceId", notNullValue()))
                .andExpect(jsonPath("$.data.traceId", is("trace-test-runtime-001")))
                .andExpect(jsonPath("$.data.capability.capabilityCode", is("capability.qa.answer")))
                .andExpect(jsonPath("$.data.agent.agentCode", is("agent.qa")))
                .andExpect(jsonPath("$.data.result.status", is("TOOL_INVOKED")))
                .andExpect(jsonPath("$.data.result.message", is("能力已完成路由并触发 mock Tool 执行")))
                .andExpect(jsonPath("$.data.result.allowedSkillCodes[0]", is("skill.qa")))
                .andExpect(jsonPath("$.data.result.output.mock", is(true)))
                .andExpect(jsonPath("$.data.result.output.prompt.resolved", is(true)))
                .andExpect(jsonPath("$.data.result.output.prompt.promptCode", is("prompt.qa.default")))
                .andExpect(jsonPath("$.data.result.output.prompt.scenarioCode", is("qa")))
                .andExpect(jsonPath("$.data.result.output.prompt.renderedPrompt",
                        is("请结合上下文与工具结果回答用户问题：什么是 Cognitive Enhancement AI?")))
                .andExpect(jsonPath("$.data.result.output.businessOutput.answer",
                        is("这是本地 Tool 返回的演示检索结果。")))
                .andExpect(jsonPath("$.data.result.output.executorType", is("TOOL")))
                .andExpect(jsonPath("$.data.result.output.invocationResult.executorType", is("TOOL")))
                .andExpect(jsonPath("$.data.result.output.invocationResult.toolCode", is("tool.search")))
                .andExpect(jsonPath("$.data.result.output.invocationResult.parameters.temperature", is(0.3)))
                .andExpect(jsonPath("$.data.result.output.invocationResult.parameters.topP", is(0.5)))
                .andExpect(jsonPath("$.data.result.output.toolResult.executorType", is("TOOL")))
                .andExpect(jsonPath("$.data.result.output.toolResult.toolCode", is("tool.search")))
                .andExpect(jsonPath("$.data.result.output.toolResult.parameters.temperature", is(0.3)))
                .andExpect(jsonPath("$.data.result.output.toolResult.parameters.topP", is(0.5)))
                .andExpect(jsonPath("$.data.result.output.toolResult.toolPayload.parameters.temperature", is(0.3)))
                .andExpect(jsonPath("$.data.result.output.toolResult.toolPayload.parameters.topP", is(0.5)));
    }

    /**
     * 验证 Prompt 缺少必要变量时会在运行时入口被显式拒绝。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRejectCapabilityWhenPromptRequiredVariableIsMissing() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                  }
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0409")))
                .andExpect(jsonPath("$.message", is("Prompt 变量缺失: question")));
    }

    /**
     * 验证输入参数类型与 Capability 输入 Schema 不匹配时会被拒绝。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRejectCapabilityWhenInputSchemaTypeIsInvalid() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": 123
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0400")))
                .andExpect(jsonPath("$.message", is("输入参数 question 必须是 string")));
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
            return () -> "trace-test-runtime-001";
        }
    }
}
