package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ReAct 工具调用集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(ReActToolCallControllerTest.ReactToolCallLlmConfiguration.class)
class ReActToolCallControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExecuteToolCallAndContinueToFinalAnswer() throws Exception {
        String traceId = "trace-react-tool-call-001";

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .header(TraceContextFilter.TRACE_ID_HEADER, traceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "请用搜索工具查询退款政策"
                                  },
                                  "parameters": {
                                    "reactMaxIterations": 2
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.result.status", is("SUCCESS")))
                .andExpect(jsonPath("$.data.result.output.executorType", is("REACT")))
                .andExpect(jsonPath("$.data.result.output.businessOutput.answer",
                        is("已根据工具结果确认：这是本地 Tool 返回的演示检索结果。")))
                .andExpect(jsonPath("$.data.result.output.reactSteps[0].toolCalls[0].name", is("tool.search")))
                .andExpect(jsonPath("$.data.result.output.reactSteps[0].observations[0].success", is(true)))
                .andExpect(jsonPath("$.data.result.output.reactSteps[0].observations[0].output.answer",
                        is("这是本地 Tool 返回的演示检索结果。")));

        mockMvc.perform(get("/api/runtime/observations/traces/{traceId}/spans", traceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.data.items[?(@.spanType == 'TOOL')]").exists())
                .andExpect(jsonPath("$.data.items[?(@.spanType == 'LLM')]").exists());
    }

    @TestConfiguration
    static class ReactToolCallLlmConfiguration {

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        @Bean
        @Primary
        LlmCredentialResolver fixedLlmCredentialResolver() {
            return apiKey -> "resolved-" + apiKey;
        }

        @Bean
        @Primary
        LlmHttpExecutor reactToolCallLlmHttpExecutor() {
            AtomicInteger callCount = new AtomicInteger();
            return request -> {
                assertTrue(request.url().endsWith("/chat/completions"));
                int currentCall = callCount.incrementAndGet();
                if (currentCall == 1) {
                    return new LlmHttpResponse(200, """
                            {
                              "choices": [
                                {
                                  "finish_reason": "tool_calls",
                                  "message": {
                                    "content": null,
                                    "tool_calls": [
                                      {
                                        "id": "call-search-1",
                                        "type": "function",
                                        "function": {
                                          "name": "tool.search",
                                          "arguments": "{\\"question\\":\\"退款政策\\"}"
                                        }
                                      }
                                    ]
                                  }
                                }
                              ],
                              "usage": {
                                "prompt_tokens": 10,
                                "completion_tokens": 2,
                                "total_tokens": 12
                              }
                            }
                            """);
                }
                assertToolMessageContainsLocalSearchResult(request.body());
                return new LlmHttpResponse(200, """
                        {
                          "choices": [
                            {
                              "finish_reason": "stop",
                              "message": {
                                "content": "已根据工具结果确认：这是本地 Tool 返回的演示检索结果。"
                              }
                            }
                          ],
                          "usage": {
                            "prompt_tokens": 20,
                            "completion_tokens": 10,
                            "total_tokens": 30
                          }
                        }
                        """);
            };
        }

        private static void assertToolMessageContainsLocalSearchResult(String body) {
            JsonNode messages = readJson(body).path("messages");
            assertTrue(messages.isArray(), "LLM 请求体应包含 messages 数组: " + body);
            for (JsonNode message : messages) {
                if ("tool".equals(message.path("role").asText())
                        && message.path("content").asText().contains("这是本地 Tool 返回的演示检索结果。")) {
                    return;
                }
            }
            fail("第二轮 LLM 请求应携带本地 Tool observation: " + body);
        }

        private static JsonNode readJson(String body) {
            try {
                return OBJECT_MAPPER.readTree(body);
            } catch (Exception ex) {
                throw new AssertionError("LLM 请求体不是合法 JSON: " + body, ex);
            }
        }
    }
}
