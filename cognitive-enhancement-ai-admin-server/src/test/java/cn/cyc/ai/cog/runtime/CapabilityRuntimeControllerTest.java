package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.support.RuntimeLlmTestDoubleConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import cn.cyc.ai.cog.core.trace.TraceIdGenerator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 能力入口集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(RuntimeLlmTestDoubleConfiguration.class)
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
                .andExpect(jsonPath("$.data.result.status", is("SUCCESS")))
                .andExpect(jsonPath("$.data.result.message", is("这是百炼返回的演示回答。")))
                .andExpect(jsonPath("$.data.result.output.businessOutput.answer",
                        is("这是百炼返回的演示回答。")))
                .andExpect(jsonPath("$.data.result.output.executorType", is("REACT")))
                .andExpect(jsonPath("$.data.result.output.executionMode", is("REACT")))
                .andExpect(jsonPath("$.data.result.output.modelCode", is("gpt-4o-mini")))
                .andExpect(jsonPath("$.data.result.output.totalTokens").value(org.hamcrest.Matchers.greaterThan(0)));
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
     * 验证流式执行接口以 SSE 事件返回能力执行结果。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldStreamCapabilityExecutionEvents() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/runtime/capabilities/execute/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "流式接口是否可用?"
                                  }
                                }
                                """))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(header().string(TraceContextFilter.TRACE_ID_HEADER, "trace-test-runtime-001"))
                .andExpect(header().string("Content-Type", containsString(MediaType.TEXT_EVENT_STREAM_VALUE)))
                .andExpect(content().string(containsString("event: STARTED")))
                .andExpect(content().string(containsString("\"type\":\"STARTED\"")))
                .andExpect(content().string(containsString("event: COMPLETED")))
                .andExpect(content().string(containsString("\"type\":\"COMPLETED\"")))
                .andExpect(content().string(containsString("\"traceId\":\"trace-test-runtime-001\"")))
                .andExpect(content().string(containsString("\"capabilityCode\":\"capability.qa.answer\"")))
                .andExpect(content().string(containsString("\"success\":true")))
                .andExpect(content().string(containsString("\"status\":\"SUCCESS\"")));
    }

    /**
     * 验证流式执行失败时通过 FAILED 事件返回标准错误码。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldStreamFailureEventWhenCapabilityExecutionFails() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/runtime/capabilities/execute/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": 123
                                  }
                                }
                                """))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(header().string(TraceContextFilter.TRACE_ID_HEADER, "trace-test-runtime-001"))
                .andExpect(header().string("Content-Type", containsString(MediaType.TEXT_EVENT_STREAM_VALUE)))
                .andExpect(content().string(containsString("event: STARTED")))
                .andExpect(content().string(containsString("event: FAILED")))
                .andExpect(content().string(containsString("\"type\":\"FAILED\"")))
                .andExpect(content().string(containsString("\"traceId\":\"trace-test-runtime-001\"")))
                .andExpect(content().string(containsString("\"success\":false")))
                .andExpect(content().string(containsString("\"code\":\"A0400\"")))
                .andExpect(content().string(containsString("\"message\":\"输入参数 question 必须是 string\"")));
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
