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

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 多轮会话上下文集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(CapabilityRuntimeConversationContextTest.ConversationContextLlmConfiguration.class)
class CapabilityRuntimeConversationContextTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPersistExecutionMessagesAndInjectHistoryIntoNextRequest() throws Exception {
        String createBody = mockMvc.perform(post("/api/runtime/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "1",
                                  "capabilityCode": "capability.qa.answer",
                                  "title": "多轮上下文"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String sessionId = objectMapper.readTree(createBody).path("data").path("sessionId").asText();

        execute(sessionId, "上一轮问题");
        execute(sessionId, "继续回答");

        mockMvc.perform(get("/api/runtime/sessions/{sessionId}/messages", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(4)))
                .andExpect(jsonPath("$.data.items[0].role", is("USER")))
                .andExpect(jsonPath("$.data.items[0].content", is("上一轮问题")))
                .andExpect(jsonPath("$.data.items[1].role", is("ASSISTANT")))
                .andExpect(jsonPath("$.data.items[1].content", is("第一轮回答")))
                .andExpect(jsonPath("$.data.items[2].role", is("USER")))
                .andExpect(jsonPath("$.data.items[2].content", is("继续回答")))
                .andExpect(jsonPath("$.data.items[3].role", is("ASSISTANT")))
                .andExpect(jsonPath("$.data.items[3].content", is("第二轮回答")));
    }

    private void execute(String sessionId, String question) throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .header(TraceContextFilter.TRACE_ID_HEADER, "trace-conversation-context")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "%s"
                                  },
                                  "parameters": {
                                    "sessionId": "%s"
                                  }
                                }
                                """.formatted(question, sessionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @TestConfiguration
    static class ConversationContextLlmConfiguration {

        @Bean
        @Primary
        LlmCredentialResolver fixedLlmCredentialResolver() {
            return apiKey -> "resolved-" + apiKey;
        }

        @Bean
        @Primary
        LlmHttpExecutor conversationContextLlmHttpExecutor() {
            AtomicInteger callCount = new AtomicInteger();
            ObjectMapper mapper = new ObjectMapper();
            return request -> {
                int current = callCount.incrementAndGet();
                assertTrue(request.url().endsWith("/chat/completions"));
                if (current == 2) {
                    assertHistoryInjected(request.body(), mapper);
                }
                String answer = current == 1 ? "第一轮回答" : "第二轮回答";
                return new LlmHttpResponse(200, """
                        {
                          "choices": [
                            {
                              "finish_reason": "stop",
                              "message": {
                                "content": "%s"
                              }
                            }
                          ],
                          "usage": {
                            "prompt_tokens": 10,
                            "completion_tokens": 5,
                            "total_tokens": 15
                          }
                        }
                        """.formatted(answer));
            };
        }

        private static void assertHistoryInjected(String body, ObjectMapper mapper) {
            try {
                JsonNode messages = mapper.readTree(body).path("messages");
                assertTrue(messages.isArray(), "LLM 请求体应包含 messages 数组: " + body);
                assertTrue(containsMessage(messages, "user", "上一轮问题"), "第二轮请求应包含上一轮用户消息: " + body);
                assertTrue(containsMessage(messages, "assistant", "第一轮回答"), "第二轮请求应包含上一轮助手消息: " + body);
                assertTrue(containsMessage(messages, "user", "继续回答"), "第二轮请求应包含当前用户消息: " + body);
            } catch (Exception ex) {
                throw new AssertionError("LLM 请求体不是合法 JSON: " + body, ex);
            }
        }

        private static boolean containsMessage(JsonNode messages, String role, String content) {
            for (JsonNode message : messages) {
                if (role.equals(message.path("role").asText())
                        && message.path("content").asText().contains(content)) {
                    return true;
                }
            }
            return false;
        }
    }
}
